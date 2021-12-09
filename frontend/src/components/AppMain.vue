<template>
  <div>
    <token-dialog
      :hasTokenSet="hasTokenSet"
      @token-save="saveToken"
    ></token-dialog>
    <new-folder-dialog
      v-model="newFolderDialogVisible"
      @save="createNewFolder"
    ></new-folder-dialog>
    <file-upload-dialog
      v-model="uploadFileDialogVisible"
      @upload="uploadFile"
    ></file-upload-dialog>
    <div class="buttons-area">
      <v-btn
        class="mr-2"
        text
        color="primary"
        @click="uploadFileDialogVisible = true"
        ><v-icon>mdi-file-image-plus</v-icon> Upload a file</v-btn
      >
      <v-btn text color="primary" @click="newFolderDialogVisible = true"
        ><v-icon>mdi-folder-plus</v-icon> Create new folder</v-btn
      >
    </div>
    <div>
      <div v-if="currentDirectory != null">
        <div v-for="file in currentItems" :key="file.id">
          <v-icon>{{ itemIcon(file) }}</v-icon>
          <span @click="handleItemClick(file)">{{ file.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import TokenDialog from "./TokenDialog.vue";
import NewFolderDialog from "./NewFolderDialog.vue";
import FileUploadDialog from "./FileUploadDialog.vue";

const toBase64 = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });

export default {
  name: "AppMain",
  components: {
    TokenDialog,
    NewFolderDialog,
    FileUploadDialog,
  },
  props: {
    user: Object, // userinfo, this component will be only rendered when this object is not null
  },
  data() {
    return {
      currentDirectory: null,
      currentItems: [],
      newFolderDialogVisible: false,
      uploadFileDialogVisible: false,
    };
  },
  computed: {
    hasTokenSet() {
      return this.user.token !== null;
    },
  },
  methods: {
    async saveToken(tokenValue) {
      // todo try-catch
      await axios.post("be/v1/tokens/gofile", {
        value: tokenValue,
      });
      this.$emit("token-saved");
    },
    async createNewFolder(folderName) {
      await axios.post("be/v1/files", {
        name: folderName,
        parentId: this.currentDirectory,
        type: "FOLDER",
      });
      this.newFolderDialogVisible = false; // todo this wont clear the input
      await this.fetchCurrentFolderContent(); // reload folder content
    },
    async uploadFile(file) {
      const base64 = await toBase64(file);
      // remove the prefix
      const base64WithoutPrefix = base64.substring(
        base64.indexOf("base64,") + "base64,".length
      );
      await axios.post("be/v1/files", {
        name: file.name,
        parentId: this.currentDirectory,
        type: "FILE",
        content: base64WithoutPrefix,
      });
       this.uploadFileDialogVisible = false; // todo this wont clear the input
      await this.fetchCurrentFolderContent(); // reload folder content
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
    async fetchCurrentFolderContent() {
      const contentResponse = await axios.get(
        `be/v1/files/${this.currentDirectory}`
      );
      if (!contentResponse.data.children) {
        throw "Invalid structure received";
      }
      this.currentItems = contentResponse.data.children;
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
        this.fetchCurrentFolderContent();
      },
    },
  },
};
</script>

<style scoped>
.buttons-area {
  display: flex;
  justify-content: flex-end;
  padding: 1rem;
}
</style>