<template>
  <v-dialog width="500" :value="!hasTokenSet" persistent>
    <v-card>
      <v-card-title class="text-h5"> Enter your API key </v-card-title>

      <v-card-text>
        <v-row>
          <v-col>
            <v-text-field
              :rules="rules"
              label="GoFile token"
              v-model="tokenInput"
            ></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue darken-1"
          text
          @click="saveToken"
          :loading="saving"
          :disabled="!valid"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "TokenDialog",
  props: {
    hasTokenSet: Boolean,
    saving: Boolean,
  },
  data() {
    return {
      tokenInput: null,
      rules: [(value) => !!value || "Required."],
    };
  },
  computed: {
    valid() {
      return !!this.tokenInput;
    },
  },
  methods: {
    saveToken() {
      if (!this.tokenInput) {
        return;
      }
      this.$emit("token-save", this.tokenInput);
    },
  },
};
</script>

<style>
</style>