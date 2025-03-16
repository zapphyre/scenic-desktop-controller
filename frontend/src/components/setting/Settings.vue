<script setup lang="ts">
import {onMounted, ref, watch} from "vue";
import {Settings} from "@/model/gpadOs";
import InputText from 'primevue/inputtext';
import ToggleSwitch from 'primevue/toggleswitch';
import apiClient from "@/api";

const localSettings = ref<Settings>({
  instanceName: "",
  allowNetworkDiscovery: false,
  baseSceneName: "",
  disconnectLocalOnRemoteConnection: false,
});

onMounted(async () => {
  localSettings.value = (await apiClient.get("settings")).data;

  watch(localSettings.value, (newVal) => {
    console.log("changed");
    apiClient.put("settings", localSettings.value);
  })
})

</script>

<template>
  <div class="card">
    <div class="grid">
      <div class="col-12 line">
        <div class="col-3 flex">
          <label for="instanceName" class="col-12 text-left pr-2">Instance Name</label>
          <InputText
              name="instanceName"
              v-model="localSettings.instanceName"
              class="col-12"
              placeholder="Enter instance name"
          />
        </div>
      </div>
      <div class="col-12 line">
        <div class="col-3 flex align-items-center">
          <label for="allowNetworkDiscovery" class="col-12 text-left">Allow Network Discovery</label>
          <ToggleSwitch
              name="allowNetworkDiscovery"
              class="col-12"
              style="padding: 0"
              v-model="localSettings.allowNetworkDiscovery"
          />
        </div>
      </div>
      <div class="col-12 line">
        <div class="col-3 flex">
          <label for="baseSceneName" class="col-12 text-left pr-2">Base Scene Name</label>
          <InputText
              name="baseSceneName"
              v-model="localSettings.baseSceneName"
              class="col-12"
              placeholder="Enter base scene name"
          />
        </div>
      </div>
      <div class="col-12">
        <div class="col-3 flex align-items-center">
          <label for="disconnectLocalOnRemoteConnection" class="col-12 text-left">Disconnect Local On Remote
            Connect</label>
          <ToggleSwitch
              name="disconnectLocalOnRemoteConnection"
              class="col-12"
              style="padding: 0"
              v-model="localSettings.disconnectLocalOnRemoteConnection"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line {
  border-bottom: 1px solid #213547;
}
</style>