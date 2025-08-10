<script setup lang="ts">
import apiClient from "@/api";
import { onMounted, ref, computed } from "vue";
import { ESourceEvent, SourceEvent } from "@/model/gpadOs";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Button from "primevue/button";
import { getSettings } from "@/api/dataStore"; // only need getSettings here

const sources = ref<SourceEvent[]>([]);
const autoconnect = ref<string[]>([]); // array of source names
let connectedEventSource: EventSource;

const toggle = async (source: SourceEvent) => {
  await apiClient.put("source/toggle", source.def);
};

// Computed binding factory for each row
const autoBinding = (source: SourceEvent) => async (checked: boolean) => {
  try {
    // Send current desired state to API
    const res = await apiClient.put(`settings/autoconnect/${source.def.name}`, checked);
    // Replace autoconnect list with updated server result
    autoconnect.value = res.data;
  } catch (err) {
    console.error("Failed to update autoconnect", err);
  }
}


onMounted(async () => {
  // 1) Load autoconnect list from settings
  const settings = await getSettings();
  console.log('settings', settings);
  autoconnect.value = settings!.autoconnect;

  // 2) Load sources list
  const source = await apiClient.get("source/all");
  sources.value = source.data;

  // 3) Subscribe to SSE
  const url = `${import.meta.env.VITE_API_BASE_URL}/source/events`;
  connectedEventSource = new EventSource(url);

  connectedEventSource.onmessage = (e: MessageEvent) => {
    const evt: SourceEvent = JSON.parse(e.data);
    const sourceIndex = sources.value.findIndex(
        (q) => q.def.name === evt.def.name
    );
    const existing = sourceIndex !== -1 ? sources.value[sourceIndex] : null;

    if (existing) {
      existing.evt = evt.evt;
    }

    const evtString = evt.evt.toUpperCase();
    if (evtString === ESourceEvent[ESourceEvent.APPEARED].toUpperCase()) {
      if (!existing) {
        sources.value.push(evt);
      }
    } else if (
        evtString === ESourceEvent[ESourceEvent.LOST].toUpperCase() &&
        sourceIndex !== -1
    ) {
      sources.value.splice(sourceIndex, 1);
    }
  };
});
</script>

<template>
  <div class="card">
    <div class="grid">
      <div class="col-12">
        <DataTable :value="sources" tableStyle="min-width: 50rem">
          <Column field="def.baseUrl" header="Location">
            <template #body="{ data }">
              {{ data.def.baseUrl + ":" + data.def.port }}
            </template>
          </Column>

          <Column field="def.name" header="Name" />

          <!-- AutoConnect column -->
          <Column header="Auto" style="width: 4rem; text-align: center;">
            <template #body="{ data }">
              <input
                  type="checkbox"
                  :checked="autoconnect.includes(data.def.name)"
                  @change="e => autoBinding(data)((e.target as HTMLInputElement).checked)"
              />
            </template>
          </Column>

          <Column field="connected" header="Active">
            <template #body="{ data }">
              <span
                  :class="
                  data.evt == ESourceEvent.CONNECTED
                    ? 'text-green-500'
                    : 'text-red-500'
                "
              >
                {{ data.evt == ESourceEvent.CONNECTED ? "Yes" : "No" }}
              </span>
            </template>
          </Column>

          <Column header="Toggle State">
            <template #body="{ data }">
              <Button
                  :label="
                  data.evt == ESourceEvent.CONNECTED
                    ? 'Disconnect'
                    : 'Connect'
                "
                  :severity="
                  data.evt == ESourceEvent.CONNECTED
                    ? 'warning'
                    : 'success'
                "
                  @click="toggle(data)"
              />
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<style scoped>
input[type="checkbox"] {
  transform: scale(1.1);
  cursor: pointer;
}
</style>
