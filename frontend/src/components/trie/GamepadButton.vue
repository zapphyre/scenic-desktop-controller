<template>
  <div class="gamepad-button" :class="buttonColor">
    <span class="button-char">{{ mappedType?.toUpperCase() }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue';

const props = defineProps<{
  character: string;
  dict?: { keys: string[]; value: "a" | "b" | "x" | "y" }[];
}>();

// Determine the button type using the mapping dictionary
const mappedType = computed<"a" | "b" | "x" | "y" | undefined>(() => {
  const inputChar = props.character.toLowerCase();

  return props.dict?.find(({ keys }) =>
      keys.some(k => k.toLowerCase() === inputChar)
  )?.value;
});

const buttonColor = computed(() => {
  switch (mappedType.value) {
    case 'x': return 'btn-x';
    case 'y': return 'btn-y';
    case 'a': return 'btn-a';
    case 'b': return 'btn-b';
    default: return 'btn-default';
  }
});
</script>

<style scoped>
.gamepad-button {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.25rem;
  color: white;
  box-shadow: inset 0 0 6px rgba(255,255,255,0.2), 0 2px 6px rgba(0,0,0,0.3);
  transition: transform 0.1s ease;
  user-select: none;
}

.gamepad-button:hover {
  transform: scale(1.05);
}

.button-char {
  pointer-events: none;
}

/* Colors */
.btn-x {
  background: radial-gradient(circle at 30% 30%, #3b82f6, #1e40af);
}
.btn-y {
  background: radial-gradient(circle at 30% 30%, #facc15, #b45309);
}
.btn-a {
  background: radial-gradient(circle at 30% 30%, #10b981, #065f46);
}
.btn-b {
  background: radial-gradient(circle at 30% 30%, #ef4444, #991b1b);
}
.btn-default {
  background: radial-gradient(circle at 30% 30%, #6b7280, #374151);
}
</style>
