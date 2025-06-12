<script setup>
import Menubar from 'primevue/menubar';
import ActionSettings from "./components/action/SceneSelect.vue";
import StateControl from "./components/state/StateControl.vue";
import SourceSelect from "./components/source/SourceSelect.vue";
import Settings from "./components/setting/Settings.vue";

import {ref} from "vue";
import GestureDef from "@/components/gesture/GestureDef.vue";

const selected = ref(ActionSettings)

const menu = ref([
  {
    label: 'Action Settings',
    icon: 'pi pi-sliders-h',
    command: () => selected.value = ActionSettings
  },
  {
    label: 'Gesture',
    icon: 'pi pi-sliders-h',
    command: () => selected.value = GestureDef
  },
  {
    label: 'State Control',
    icon: 'pi pi-objects-column',
    command: () => selected.value = StateControl
  },
  {
    label: 'Sources Management',
    icon: 'pi pi-sitemap',
    command: () => selected.value = SourceSelect
  },
  {
    label: 'Instance Settings',
    icon: 'pi pi-hammer',
    command: () => selected.value = Settings
  }
]);

</script>


<template>
  <div class="top-navigation">
    <Menubar :model="menu" class="fixed-top"/>

    <component class="content" :is="selected"></component>
  </div>

</template>

<style scoped>

.top-navigation {
  width: 100%; /* Full width of the parent */
  min-height: 100vh; /* Allow content to expand full height */
  display: flex;
  flex-direction: column;
}

/* Fix the Menubar at the top and make it full-width */
.fixed-top {
  position: fixed;
  top: 0;
  left: 0; /* Ensure it starts at the left edge */
  right: 0; /* Ensure it extends to the right edge */
  z-index: 1000; /* Keep it above other content */
  margin: 0; /* Remove any default margins */
}
</style>
