<script setup lang="ts">

import type {Lang} from "@/model/gpadOs";
import Dialog from "primevue/dialog";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import {computed, ref} from "vue";

const newLang = ref<Lang>({} as Lang);

const okAndClose = () => {
  emit('update:visible', false);
  emit('ok', newLang.value);
}

const props = defineProps<{
  visible: boolean;
}>();

const localVisible = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});

const emit = defineEmits<{
  ok: [lang: Lang];
  'update:visible': [value: boolean];
}>();
</script>

<template>
  <div class="card flex justify-center">
    <Dialog
        v-model:visible="localVisible"
        modal
        :closable="true"
        header="Add Language"
        :style="{ width: '25rem' }"
    >
      <!-- Empty spacer -->
      <span class="text-surface-500 dark:text-surface-400 block mb-4"></span>

      <!-- Form Fields in Grid -->
      <div class="grid flex-column gap-1">
        <!-- Language Code -->
        <div class="col-12 grid form-row">
          <label for="code" class="form-label">Language Code</label>
          <div class="form-input">
            <InputText
                id="code"
                v-model="newLang.code"
                autocomplete="off"
            />
          </div>
        </div>

        <!-- Name -->
        <div class="col-12 grid form-row">
          <label for="name" class="form-label">Name</label>
          <div class="form-input">
            <InputText
                id="name"
                v-model="newLang.name"
                autocomplete="off"
            />
          </div>
        </div>

        <!-- Buttons: Save (left), Delete (right) -->
        <div class="col-12 flex justify-content-between">
          <Button type="button" label="Save" @click="okAndClose" />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<style scoped>
.form-row {
  display: grid;
  grid-template-columns: 150px 1fr; /* Fixed 150px label, remaining space for input */
  align-items: center;
  gap: 10px; /* Optional: Space between label and input */
}

.form-label {
  font-weight: bold;
  text-align: left;
  padding: 0.5rem 0;
  white-space: nowrap; /* Prevents label text from wrapping */
}

.form-input {
  width: 100%; /* Ensures input takes remaining space */
}

.form-input :deep(.p-inputtext) {
  width: 100%; /* Targets PrimeVue InputText internally */
  box-sizing: border-box; /* Ensures padding and borders are included in width */
}
</style>