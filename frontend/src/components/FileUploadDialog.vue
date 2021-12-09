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
            ></v-file-input>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="localValue = false"> Cancel </v-btn>
        <!-- todo loading state for the button when it is saving -->
        <v-btn color="blue darken-1" text @click="upload"> Upload </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>

export default {
  name: "FileUploadDialog",
  props: {
    value: Boolean,
  },
  data() {
    return {
      file: null,
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
  },
  methods: {
    upload() {
      // todo check maximum size
      this.$emit('upload', this.file)
    },
  },
};
</script>

<style>
</style>