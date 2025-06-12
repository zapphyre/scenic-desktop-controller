<script setup lang="ts">

import {onMounted} from "vue";
import {getGestures} from "@/api/store";
import {ref} from 'vue';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';

// const gestures = getGestures();

// State for gestures
const gestures = ref([]);

// Add a new gesture
const addGesture = () => {
  gestures.value.push({
    name: '',
    paths: [],
  });
};

// Add a path to a specific gesture
const addPath = (gestureIndex) => {
  gestures.value[gestureIndex].paths.push('');
};

// Handle name input changes
const onNameChange = (gestureIndex) => {
  // Optionally emit or handle changes
  console.log(`Gesture ${gestureIndex} name updated:`, gestures.value[gestureIndex].name);
};

// Handle path input changes
const onPathChange = (gestureIndex) => {
  // Optionally emit or handle changes
  console.log(`Gesture ${gestureIndex} paths updated:`, gestures.value[gestureIndex].paths);
};

onMounted(() => {

});
</script>

<template>
  <div class="p-4">
    <!-- Add Gesture Button -->
    <div class="mb-4 flex justify-content-center">
      <Button label="Add Gesture" icon="pi pi-plus" @click="addGesture"/>
    </div>

    <!-- Gesture Grid -->
    <div class="grid" v-for="(gesture, index) in gestures" :key="index">
      <div class="col-12">
        <div class="card p-3">
          <!-- Make this row flex horizontally -->
          <div class="flex align-items-start gap-3">
            <!-- Name Input and Button -->
            <div class="col-4">
              <div class="flex flex-column gap-2">
                <InputText
                    v-model="gesture.name"
                    placeholder="Gesture Name"
                    @input="onNameChange(index)"
                />
                <Button
                    label="Add Path"
                    icon="pi pi-plus"
                    @click="addPath(index)"
                />
              </div>
            </div>

            <!-- Path Inputs -->
            <div class="col-3">
              <div class="flex flex-column gap-2">
                <InputText
                    v-for="(path, pathIndex) in gesture.paths"
                    :key="pathIndex"
                    v-model="gesture.paths[pathIndex]"
                    placeholder="Path"
                    @input="onPathChange(index)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>

</style>