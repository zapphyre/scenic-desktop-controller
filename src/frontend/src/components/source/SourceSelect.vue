<script setup lang="ts">
import apiClient from "@/api";
import {onMounted, ref} from "vue";
import type {Scene, SourceState} from "@/model/gpadOs";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';

const sources = ref<SourceState[]>([]);

const toggle = async (source: SourceState) => {
  await apiClient.put('source/toggle', source.def);
}

onMounted(async () => {
  const source = await apiClient.get("source/all");
  sources.value = source.data;

  console.log(sources.value);
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
        <span :class="data.connected ? 'text-green-500' : 'text-red-500'">
          {{ data.connected ? 'Yes' : 'No' }}
        </span>
            </template>
          </Column>
          <Column header="Toggle State">
            <template #body="{ data }">
              <Button
                  :label="data.connected ? 'Disconnect' : 'Connect'"
                  :severity="data.connected ? 'warning' : 'success'"
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