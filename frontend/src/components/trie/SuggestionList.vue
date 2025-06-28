<script setup lang="ts">
import {computed, ref, defineProps, watch} from 'vue';
import { ValueFrequency } from '@/model/gpadOs';
import Button from 'primevue/button';

const props = defineProps<{
  suggestions: ValueFrequency[];
  rows: number;
}>();

const page = ref(0);
const start = computed(() => page.value * props.rows);
const end = computed(() => start.value + props.rows);
const paginated = computed(() => props.suggestions.slice(start.value, end.value));

const totalPages = computed(() =>
    Math.max(1, Math.ceil(props.suggestions.length / props.rows))
);

// Watch for changes in suggestions and reset page to 0
watch(() => props.suggestions, () => {
  page.value = 0;
}, { deep: true });

const onUp = (item: ValueFrequency) => {
  emit('freqIncrement', item.value);
  item.frequency = item.frequency + 1;
};

const onDown = (item: ValueFrequency) => {
  emit('freqDecrement', item.value);
  item.frequency = item.frequency - 1;
};

const onRemove = (item: ValueFrequency) => {
  emit('remove', item.value);
};

const emit = defineEmits<{
  (e: 'freqIncrement', value: string): void;
  (e: 'freqDecrement', value: string): void;
  (e: 'remove', value: string): void;
}>();
</script>

<template>
  <div class="suggestion-list-container p-2">
    <div
        v-for="(item, index) in paginated"
        :key="index"
        class="grid align-items-center mb-1 suggestion-row"
    >
      <!-- Buttons (Up/Down) and Frequency -->
      <div class="col-3 flex align-items-center gap-1">
        <Button
            icon="pi pi-arrow-up"
            class="p-button-sm p-button-text"
            @click="onUp(item)"
        />
        <span class="text-xs text-gray-400">{{ item.frequency }}</span>
        <Button
            icon="pi pi-arrow-down"
            class="p-button-sm p-button-text"
            @click="onDown(item)"
        />
      </div>

      <!-- Word -->
      <div class="col-6 text-sm text-white overflow-hidden text-ellipsis">
        {{ item.value }}
      </div>

      <!-- Delete Button -->
      <div class="col-3 flex justify-end">
        <Button
            icon="pi pi-times"
            class="p-button-sm p-button-text p-button-danger"
            @click="onRemove(item)"
        />
      </div>
    </div>

    <!-- Pagination -->
    <div class="flex justify-content-center items-center gap-3 mt-2">
      <Button
          icon="pi pi-angle-left"
          class="p-button-sm p-button-text"
          :disabled="page === 0"
          @click="page--"
      />
      <span class="text-xs text-gray-400">
        Page {{ page + 1 }} / {{ totalPages }}
      </span>
      <Button
          icon="pi pi-angle-right"
          class="p-button-sm p-button-text"
          :disabled="end >= suggestions.length"
          @click="page++"
      />
    </div>
  </div>
</template>

<style scoped>
.suggestion-list-container {
  max-height: 100%;
  background-color: transparent;
  overflow-y: auto;
  min-height: 12rem;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 6px;
}
.suggestion-row {
  padding: 0.25rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}
</style>