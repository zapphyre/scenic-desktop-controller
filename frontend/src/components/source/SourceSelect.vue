<script setup lang="ts">
import apiClient from "@/api";
import {onMounted, ref} from "vue";
import {ESourceEvent, SourceEvent} from "@/model/gpadOs";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';

const sources = ref<SourceEvent[]>([]);
let connectedEventSource: EventSource;

const toggle = async (source: SourceEvent) => {
  await apiClient.put('source/toggle', source.def);
}

onMounted(async () => {
  const source = await apiClient.get("source/all");
  sources.value = source.data;

  console.log(sources.value);

  connectedEventSource = new EventSource(`${import.meta.env.VITE_API_BASE_URL}/source/events`);
  connectedEventSource.onmessage = (e: MessageEvent) => {
    const evt: SourceEvent = JSON.parse(e.data);

    // Find the existing source by name
    const sourceIndex = sources.value.findIndex(q => q.def.name === evt.def.name);
    const source = sourceIndex !== -1 ? sources.value[sourceIndex] : null;

    if (source) {
      // Update the event type of the existing source
      source.evt = evt.evt;
    }

    // Compare server string with enum string representation
    const evtString = evt.evt.toUpperCase();
    if (evtString === ESourceEvent[ESourceEvent.APPEARED].toUpperCase()) {
      // Only push if it’s not already in the array
      if (!source) {
        sources.value.push(evt); // New source appeared
      }
      // If it’s already there, we’ve updated it above—no action needed
    } else if (evtString === ESourceEvent[ESourceEvent.LOST].toUpperCase()) {
      // Remove the source if it exists
      if (sourceIndex !== -1) {
        sources.value.splice(sourceIndex, 1);
      }
    }
  }
})
</script>

<template>

  <div class="card">
    <div class="grid">
      <div class="col-12">
        <DataTable :value="sources" tableStyle="min-width: 50rem">
          <Column field="def.baseUrl" header="Location">
            <template #body="{ data }">
              {{ data.def.baseUrl + ':' + data.def.port }}
            </template>
          </Column>
          <Column field="def.name" header="Name"></Column>
          <Column field="connected" header="Active">
            <template #body="{ data }">
        <span :class="data.evt == ESourceEvent.CONNECTED ? 'text-green-500' : 'text-red-500'">
          {{ data.evt == ESourceEvent.CONNECTED ? 'Yes' : 'No' }}
        </span>
            </template>
          </Column>
          <Column header="Toggle State">
            <template #body="{ data }">
              <Button
                  :label="data.evt == ESourceEvent.CONNECTED ? 'Disconnect' : 'Connect'"
                  :severity="data.evt == ESourceEvent.CONNECTED ? 'warning' : 'success'"
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

</style>