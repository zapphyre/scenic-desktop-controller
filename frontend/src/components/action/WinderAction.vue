<script setup lang="ts">

import ActionSection from "@/components/action/ActionSection.vue";
import {onMounted, ref} from "vue";
import {getWinderActions} from "@/api/dataStore";
import {XdoAction} from "@/model/gpadOs";

const strokes = ref<string[]>([]);

const props = defineProps<{
  xdoAction: XdoAction;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  remove: [xdoAction: XdoAction];
  addKeyStroke: [stroke: string];
}>();

onMounted(async () => {
  strokes.value = await getWinderActions();
})
</script>

<template>
  <ActionSection v-if="strokes.length"
                 :disabled="props.disabled" :xdo-action="props.xdoAction" :strokes="strokes"
                 @add-key-stroke="($e: string) => emit('addKeyStroke', $e)"
                 @remove="($e: XdoAction) => emit('remove', $e)"
  />
</template>

<style scoped>

</style>