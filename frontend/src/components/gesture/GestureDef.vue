<script setup lang="ts">

import {onMounted} from "vue";
import {getGestures, addGesture} from "@/api/dataStore";
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import apiClient from '@/api';
import type {Gesture, GesturePath} from "@/model/gpadOs";

const gestures = getGestures();

// Add a new gesture
const addNewGesture = async () => {
  const id = (await apiClient.post("gestures", {})).data;
  addGesture({id, paths: [], name: "", uiPaths: []} as Gesture);
};

// Add a path to a specific gesture
const addPath = (gesture: Gesture) => {
  const eventSource = new EventSource(`${import.meta.env.VITE_API_BASE_URL}/gestures/newPath/${gesture.id}/LEFT`);
  const gesturePath = {path: "[receiving path]"} as GesturePath;

  gesture.paths.push(gesturePath);

  eventSource.onmessage = (e: MessageEvent) => {
    gesture.paths.splice(gesture.paths.indexOf(gesturePath), 1);
    console.log('received gesturePaht', e.data);
    gesture.paths.push(JSON.parse(e.data) as GesturePath);
    console.log("gesture.paths", gesture.paths);
    eventSource.close();
  }

  eventSource.onerror = (e: Event) => {
    console.log("have error", e);
    gesture.paths.splice(gesture.paths.indexOf(gesturePath), 1);
  }
};

// Handle name input changes
const onNameChange = async (gesture: Gesture) => {
  await apiClient.put(`gestures/update-name/${gesture.id}/${gesture.name}`);
};

// Handle path input changes
const onPathChange = async (path: GesturePath) => {
  console.log("newPath", path.path);
  await apiClient.put(`gestures/update-path/${path.id}/${path.path}`);
};

// Remove a path from a specific gesture
const removePath = async (gesture: Gesture, uiPath: GesturePath, idx: number) => {
  await apiClient.delete(`gestures/delete-path/${uiPath.id}`)
  gesture.paths.splice(idx, 1);
};

const removeGesture = async (gesture: Gesture) => {
  await apiClient.delete(`gestures/${gesture.id}`)
  gestures.splice(gestures.indexOf(gesture), 1);
}
// Edit a path (toggle receiving state)
const editPath = (uiPath: GesturePath) => {
  uiPath.edit = !uiPath.edit;
};

onMounted(() => {

});
</script>

<template>
  <div class="p-4">
    <!-- Add Gesture Button -->
    <div class="grid">
      <div class="col-12">
        <div class="mb-4 flex justify-content-center">
          <Button label="Add Gesture" icon="pi pi-plus" @click="addNewGesture"/>
        </div>
      </div>
    </div>
    <!-- Gesture Grid -->
    <div class="grid" v-for="(gesture, index) in gestures" :key="index">
      <div class="col-12">
        <div class="card p-3">
          <!-- Horizontal row for name and buttons -->
          <div class="flex align-items-start gap-3">
            <!-- Name Input and Add Path Button -->
            <div class="col-4">
              <div class="flex flex-column gap-2">
                <div class="flex align-items-center gap-2">
                  <Button
                      icon="pi pi-trash"
                      class="p-button-danger p-button-sm"
                      @click="removeGesture(gesture)"
                  />

                  <InputText
                      v-model="gesture.name"
                      placeholder="Gesture Name"
                      class="w-full"
                      @input="onNameChange(gesture)"
                  />
                </div>
                <Button
                    label="Add Path"
                    icon="pi pi-plus"
                    @click="addPath(gesture)"
                />
              </div>
            </div>

            <!-- Path Inputs with Remove and Edit Buttons -->
            <div class="col-3">
              <div class="flex flex-column gap-2">
                <div
                    v-for="(path, pathIndex) in gesture.paths"
                    :key="pathIndex"
                    class="flex align-items-center gap-2"
                >
                  <Button
                      icon="pi pi-trash"
                      class="p-button-danger p-button-sm"
                      @click="removePath(gesture, path, pathIndex)"
                  />
                  <InputText
                      v-model="path.path"
                      :disabled="!path.edit"
                      placeholder="Path"
                      class="w-full"
                      @input="onPathChange(path)"
                  />
                  <Button
                      icon="pi pi-pencil"
                      class="p-button-sm"
                      @click="editPath(path)"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
        <hr/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>