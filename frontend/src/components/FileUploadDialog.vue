<template>
  <v-dialog width="500" v-model="localValue">
    <v-card>
      <v-card-title class="text-h5"> Upload new file </v-card-title>

      <v-card-text>
        <v-row>
          <v-col>
            <v-file-input
              show-size
              truncate-length="20"
              label="File to upload"
              v-model="file"
              :rules="rules"
            ></v-file-input>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="localValue = false"> Cancel </v-btn>
        <v-btn color="blue darken-1" text @click="upload" :loading="uploading" :disabled="!valid">
          Upload
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "FileUploadDialog",
  props: {
    value: Boolean,
    uploading: Boolean,
  },
  data() {
    return {
      file: null,
      rules: [
        (value) => !!value || "Required",
        (value) =>
          (!!value && value.size < 5000000) || "Maximum file size exceeded.",
      ],
    };
  },
  computed: {
    localValue: {
      get() {
        return this.value;
      },
      set(newValue) {
        this.file = null;
        this.$emit("input", newValue);
      },
    },
    valid() {
      return !!this.file && this.file.size < 5000000;
    },
  },
  methods: {
    upload() {
      if (!this.valid) {
        return;
      }
      this.$emit("upload", this.file);
    },
  },
};
</script>

<style>
</style>