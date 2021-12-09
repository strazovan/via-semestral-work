<template>
  <div>
    <v-dialog width="500" :value="!hasTokenSet" persistent>
      <v-card>
        <v-card-title class="text-h5"> Enter your API key </v-card-title>

        <v-card-text>
          <v-row>
            <v-col>
              <v-text-field
                label="GoFile token"
                v-model="tokenInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="saveToken"> Save </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <div v-if="currentDirectory != null">
      <div v-for="file in currentItems" :key="file.id">
        <v-icon>{{ itemIcon(file) }}</v-icon>
        <span @click="handleItemClick(file)">{{ file.name }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AppMain",
  props: {
    user: Object, // userinfo, this component will be only rendered when this object is not null
  },
  data() {
    return {
      tokenInput: null,
      currentDirectory: null,
      currentItems: [],
    };
  },
  computed: {
    hasTokenSet() {
      return this.user.token !== null;
    },
  },
  methods: {
    async saveToken() {
      // todo try-catch
      await axios.post("be/v1/tokens/gofile", {
        value: this.tokenInput,
      });
      this.$emit("token-saved");
    },
    itemIcon(item) {
      return item.type === "FOLDER" ? "mdi-folder" : "mdi-file";
    },
    handleItemClick(item) {
      if (item.type === "FOLDER") {
        // eslint-disable-next-line no-console
        console.log(`changing directory to ${item.id}`);
        this.currentDirectory = item.id;
      } else {
        // todo what to do?
      }
    },
  },
  watch: {
    user: {
      immediate: true,
      handler(newValue) {
        this.currentDirectory = newValue.rootFolder;
      },
    },
    currentDirectory: {
      immediate: true,
      async handler() {
        const contentResponse = await axios.get(
          `be/v1/files/${this.currentDirectory}`
        );
        if (!contentResponse.data.children) {
          throw "Invalid structure received";
        }
        this.currentItems = contentResponse.data.children;
      },
    },
  },
};
</script>

<style scoped>
</style>