<template>
  <v-dialog width="500" v-model="localValue">
    <v-card>
      <v-card-title class="text-h5"> Create new folder </v-card-title>

      <v-card-text>
        <v-row>
          <v-col>
            <v-text-field
              label="Folder name"
              v-model="folderName"
            ></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="localValue = false"> Cancel </v-btn>
        <v-btn color="blue darken-1" text @click="create" :loading="creating">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "NewFolderDialog",
  props: {
    value: Boolean,
    creating: Boolean,
  },
  data() {
    return {
      folderName: null,
    };
  },
  computed: {
    localValue: {
      get() {
        return this.value;
      },
      set(newValue) {
        // reset the input
        this.folderName = null;
        this.$emit("input", newValue);
      },
    },
  },
  methods: {
    create() {
      this.$emit("save", this.folderName);
    },
  },
};
</script>

<style>
</style>