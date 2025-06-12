<script setup lang="ts">
import Select from 'primevue/select';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import {axisValues} from "@/model/gpadOs";
import type {Scene} from "@/model/gpadOs";
import {computed} from "vue";

const localVisible = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});

const okAndClose = () => {
  emit('update:visible', false);
  emit('ok', props.scene);
}

const deleteAndClose = () => {
  emit('update:visible', false);
  emit('delete', props.scene);
}

const props = withDefaults(defineProps<{
  scene: Scene;
  inheritsAvail: string[]
  isEdit?: boolean;
  visible: boolean;
}>(), {
  isEdit: false,
});

const emit = defineEmits<{
  ok: [scene: Scene];
  delete: [scene: Scene];
  'update:visible': [value: boolean];
}>();


// Define label column width (adjustable here)
const labelColWidth = 4; // e.g., col-3 (25% of 12-column grid)

// Computed classes for label and input columns
const labelColClass = computed(() => `col-${labelColWidth}`);
const inputColClass = computed(() => `col-${12 - labelColWidth}`);

</script>

<template>
  <div class="card flex justify-center">
    <Dialog
        v-model:visible="localVisible"
        modal
        :closable="true"
        :header="props.isEdit ? `Edit: ${props.scene.name}` : 'Add New Action'"
        :style="{ width: '25rem' }"
    >
      <!-- Empty spacer -->
      <span class="text-surface-500 dark:text-surface-400 block mb-4"></span>

      <!-- Form Fields in Grid -->
      <div class="grid flex-column gap-1">
        <!-- Name -->
        <div class="col-12 grid">
          <label
              for="name"
              :class="labelColClass"
              class="font-semibold text-left pr-2">Name</label>
          <div :class="inputColClass" class="p-0">
            <InputText
                id="name"
                v-model="props.scene.name"
                class="w-full"
                autocomplete="off"
            />
          </div>
        </div>

        <!-- Window Name -->
        <div class="col-12 grid">
          <label
              for="wname"
              :class="labelColClass"
              class="font-semibold text-left pr-2">Window Name</label>
          <div :class="inputColClass" class="p-0">
            <InputText
                id="wname"
                v-model="props.scene.windowName"
                class="w-full"
                autocomplete="off"
            />
          </div>
        </div>

        <!-- Inherits From -->
        <div class="col-12 grid">
          <label
              for="inherits"
              :class="labelColClass"
              class="font-semibold text-left pr-2">Inherits From</label>
          <div :class="inputColClass" class="p-0">
            <Select
                id="inherits"
                v-model="props.scene.inheritsIdFk"
                :options="props.inheritsAvail"
                placeholder="Inherits from Scene"
                class="w-full"
            />
          </div>
        </div>

        <!-- Left Axis -->
        <div class="col-12 grid">
          <label
              for="leftAxis"
              :class="labelColClass"
              class="font-semibold text-left pr-2">Left Axis</label>
          <div :class="inputColClass" class="p-0">
            <Select
                id="leftAxis"
                v-model="props.scene.leftAxisEvent"
                :options="axisValues"
                class="w-full"
            />
          </div>
        </div>

        <!-- Right Axis -->
        <div class="col-12 grid">
          <label
              for="rightAxis"
              :class="labelColClass"
              class="font-semibold text-left pr-2">Right Axis</label>
          <div :class="inputColClass" class="p-0">
            <Select
                id="rightAxis"
                v-model="props.scene.rightAxisEvent"
                :options="axisValues"
                class="w-full"
            />
          </div>
        </div>

        <!-- Buttons: Save (left), Delete (right) -->
        <div class="col-12 flex items-center">
          <Button type="button" label="Save" @click="okAndClose"/>
          <Button
              v-if="isEdit"
              type="button"
              label="Delete"
              severity="danger"
              class="ml-auto"
              @click="deleteAndClose"
          />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<style scoped>

</style>