<template>
  <div>
    <v-dialog width="500" :value="!hasTokenSet">
      <v-card>
        <v-card-title class="text-h5">
          Enter your API key
        </v-card-title>

        <v-card-text>
          TODO INPUT
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text>
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: "AppMain",
  props: {
    user: Object, // userinfo, this component will be only rendered when this object is not null
  },
  computed: {
    hasTokenSet() {
      return this.user.tokens.gofile !== null;
    },
  },
};
</script>

<style scoped>
</style>