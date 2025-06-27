<script setup lang="ts">
import LangDialog from "@/components/trie/LangDialog.vue";
import {computed, ref} from "vue";
import {Lang, TrieResult, ValueFrequency} from "@/model/gpadOs";
import {getLanguages} from "@/api/dataStore";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import Select from "primevue/select";
import type {FileUploadUploadEvent} from 'primevue/fileupload';
import FileUpload from 'primevue/fileupload';
import Button from 'primevue/button';
import apiClient from "@/api";
import SuggestionList from "@/components/trie/SuggestionList.vue";
import GamepadButton from "@/components/trie/GamepadButton.vue";

const dialogVisible = ref(false);
const langs = getLanguages();
// langs.push({name: "slovak", id: 2} as Lang); // Ensure this matches Lang structure
const selected = ref<Lang>(langs[0]);

const base = import.meta.env.VITE_API_BASE_URL;
console.log("base", base);

const suggestTerm = ref('');
const suggestions = ref<ValueFrequency[]>([]);
const encoded = ref<string>();

const dict: { keys: string[]; value: "x" | "a" | "b" | "y" }[] = [
  {keys: ['q', 't'], value: 'y'},
  {keys: ['w', 'y'], value: 'b'},
  {keys: ['e', 'u'], value: 'a'},
  {keys: ['r', 'i'], value: 'x'}
];

const fetchSuggestions = async () => {
  if (!selected.value?.id) return;

  try {
    const res = await apiClient.get<TrieResult>(`languages/${selected.value.id}/suggest/${encodeURIComponent(suggestTerm.value)}`);
    suggestions.value = res.data.trie;
    encoded.value = res.data.encoded;
    console.log('encoded.value', encoded.value);
  } catch (e) {
    console.error("Suggestion fetch error", e);
    suggestions.value = [];
    encoded.value = "";
  }
};

const dialogOk = async (q: Lang) => {
  selected.value = q;
  selected.value.id = (await apiClient.post(`languages`, q)).data;
  langs.push(selected.value);
};

const changed = async (q: Event) => {
  await apiClient.put(`languages`, selected.value);
}

const remove = async () => {
  await apiClient.delete(`languages/${selected.value.id}`);
  langs.splice(langs.indexOf(selected.value), 1);
}

const showEmbeddedMessage = computed(() =>
    suggestTerm.value.trim().length > 0 && suggestions.value.length === 0
);

const onButtonClick = () => {
  console.log('User clicked the "no results" button');
};

const onUpload = (event: FileUploadUploadEvent) => {
  const responseText = event.xhr.response;

  try {
    selected.value.size = responseText
  } catch (e) {
    console.error("Invalid JSON in upload response", e);
  }
}

const propUpWord = async (word: string) => {
  await apiClient.put(`adjust/${selected.value.id}/increment/${word}`);
}

const propDownWord = async (word: string) => {
  await apiClient.put(`adjust/${selected.value.id}/decrement/${word}`);
}

</script>

<template>
  <LangDialog @ok="dialogOk" v-model:visible="dialogVisible"/>

  <div class="p-grid">
    <div class="col-12">
      <div class="card p-3">

        <!-- First row: Action buttons and file upload -->
        <div class="flex justify-content-center mb-3">
          <div class="grid w-full">
            <div class="col-1 flex align-items-end">
              <Button
                  label="RM"
                  icon="pi pi-trash"
                  class="p-button-danger p-button-sm w-full"
                  @click="remove"
              />
            </div>

            <div class="col-3">
              <FloatLabel class="w-full">
                <Select
                    name="langSelect"
                    v-model="selected"
                    :options="langs"
                    optionLabel="name"
                    placeholder="Select a Language"
                    class="w-full"
                />
                <label for="langSelect">Language</label>
              </FloatLabel>
            </div>

            <div class="col-2 flex align-items-end">
              <Button
                  label="Edit"
                  icon="pi pi-pencil"
                  class="w-full"
                  @click="dialogVisible = !dialogVisible"
              >
                Add new
              </Button>
            </div>

            <div class="col-2">
              <FileUpload
                  name="file"
                  :url="base + `/languages/init/${selected.id}`"
                  accept="*"
                  :auto="true"
                  mode="basic"
                  :multiple="true"
                  class="w-full"
                  @upload="onUpload"
              />
            </div>
          </div>
        </div>

        <!-- Second row: Form (Name, Code, Size) -->
        <div v-if="selected" class="flex justify-content-center">
          <div class="p-fluid grid w-full">
            <div class="col-12 md:col-6">
              <InputText
                  v-model="selected.name"
                  placeholder="Language Name"
                  class="w-full"
                  @input="changed"
              />
            </div>

            <div class="col-12 md:col-4">
              <InputText
                  v-model="selected.code"
                  placeholder="Language Code"
                  class="w-full"
                  @input="changed"
              />
            </div>

            <div class="col-12 md:col-2 flex align-items-center">
              <span class="p-text-bold">Size:</span>
              <span class="ml-2">{{ selected.size || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- Spacer rows (optional, could be removed) -->
        <div class="col-12"></div>
        <div class="col-12"></div>
        <div class="col-12"></div>
        <div class="col-12"></div>

        <!-- Third row: InputText and GamepadButton and SuggestionList -->
        <div v-if="selected" class="flex justify-content-center">
          <div class="grid w-full">
            <div class="col-offset-2 col-4">
              <div class="relative w-full">
                <!-- Tag-like "Nothing found" -->
                <div
                    v-if="showEmbeddedMessage"
                    class="nothing-found-chip"
                >
                  Nothing found
                </div>

                <!-- Input -->
                <InputText
                    v-model="suggestTerm"
                    placeholder="Type something..."
                    class="w-full"
                    @input="fetchSuggestions"
                />

                <!-- Gamepad buttons, centered under input -->
                <div class="flex justify-content-center gap-2 mt-2">
                  <GamepadButton
                      v-for="(char, index) in encoded"
                      :key="index"
                      :character="char"
                      :dict="dict"
                  />
                </div>
              </div>
            </div>

            <div class="col-4">
              <SuggestionList @freq-increment="propUpWord"
                              @freq-decrement="propDownWord"
                              :suggestions="suggestions" :rows="5"/>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

