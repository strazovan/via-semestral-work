<template>
  <div>
    <v-dialog width="500" :value="!hasTokenSet" persistent>
      <v-card>
        <v-card-title class="text-h5"> Enter your API key </v-card-title>

        <v-card-text>
          <v-row>
            <v-col>
              <v-text-field label="GoFile token" v-model="tokenInput"></v-text-field>
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
      <div v-for="file in folderContent" :key="file.id">
        <v-icon>{{ itemIcon(file) }}</v-icon>
        <span @click="handleItemClick(file)">{{ file.name }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: "AppMain",
  props: {
    user: Object, // userinfo, this component will be only rendered when this object is not null
  },
  data() {
    return {
      tokenInput: null,
      currentDirectory: null,
    };
  },
  computed: {
    hasTokenSet() {
      return this.user.token !== null;
    },
    folderContent() {
      // eslint-disable-next-line no-console
      console.log(this.currentDirectory); // todo fetch the content of the directory
      return [
        {
          id: "sdlkfafkj",
          name: "test file",
          type: "FILE",
          created: new Date(),
          size: 453,
          children: [],
        },
        {
          id: "d'f;lps;s",
          name: "test folder",
          type: "FOLDER",
          created: new Date(),
          size: 0,
          children: [],
        },
      ];
    },
  },
  methods: {
    async saveToken() {
      // todo try-catch
      await axios.post("be/v1/tokens/gofile", {
        value: this.tokenInput
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
  },
};
</script>

<style scoped>
</style>