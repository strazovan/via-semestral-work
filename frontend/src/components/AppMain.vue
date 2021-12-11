<template>
  <div>
    <token-dialog
      :hasTokenSet="hasTokenSet"
      @token-save="saveToken"
      :saving="saving"
    ></token-dialog>
    <new-folder-dialog
      v-model="newFolderDialogVisible"
      @save="createNewFolder"
      :creating="creating"
    ></new-folder-dialog>
    <file-upload-dialog
      v-model="uploadFileDialogVisible"
      @upload="uploadFile"
      :uploading="uploading"
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
    <div class="main-content">
      <div class="items-container">
        <div v-if="currentDirectory != null">
          <div v-if="parentFolder != null" class="item">
            <div>
              <v-icon class="item-icon">mdi-arrow-up-left</v-icon>
              <span class="link" @click="goUp()">..</span>
            </div>
          </div>
          <div v-for="file in currentItems" :key="file.id" class="item">
            <div>
              <v-icon class="item-icon">{{ itemIcon(file) }}</v-icon>
              <span class="link" @click="handleItemClick(file)">{{
                file.name
              }}</span>
            </div>
            <div>
              <v-icon class="delete-icon" @click="deleteItem(file)"
                >mdi-delete-outline</v-icon
              >
            </div>
          </div>
        </div>
      </div>
      <div v-if="showNoContent" class="no-content text-h5">
        Start by creating a folder or upload a new file
      </div>
      <v-overlay :value="loading">
        <v-progress-circular indeterminate size="64"></v-progress-circular>
      </v-overlay>
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
      path: [],
      currentDirectory: null,
      currentItems: [],
      newFolderDialogVisible: false,
      uploadFileDialogVisible: false,
      loading: false,
      uploading: false,
      creating: false,
      saving: false,
    };
  },
  computed: {
    hasTokenSet() {
      return this.user.token !== null;
    },
    parentFolder() {
      return this.path.length > 0 ? this.path[this.path.length - 1] : null;
    },
    showNoContent() {
      return (
        !this.loading &&
        this.hasTokenSet &&
        this.parentFolder == null &&
        this.currentItems.length === 0
      );
    },
  },
  methods: {
    async saveToken(tokenValue) {
      this.saving = true;
      // todo try-catch
      await axios.post("be/v1/tokens/gofile", {
        value: tokenValue,
      });
      this.saving = false;
      this.$emit("token-saved");
    },
    async createNewFolder(folderName) {
      this.creating = true;
      await axios.post("be/v1/files", {
        name: folderName,
        parentId: this.currentDirectory,
        type: "FOLDER",
      });
      this.creating = false;
      this.newFolderDialogVisible = false; // todo this wont clear the input
      await this.fetchCurrentFolderContent(); // reload folder content
    },
    async uploadFile(file) {
      this.uploading = true;
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
      this.uploading = false;
      this.uploadFileDialogVisible = false; // todo this wont clear the input
      await this.fetchCurrentFolderContent(); // reload folder content
    },
    itemIcon(item) {
      return item.type === "FOLDER" ? "mdi-folder" : "mdi-file";
    },
    goUp() {
      const parent = this.path.splice(-1);
      this.currentDirectory = parent;
    },
    async handleItemClick(item) {
      if (item.type === "FOLDER") {
        this.path.push(this.currentDirectory);
        this.currentDirectory = item.id;
      } else {
        const contentHolder = await axios.get(`be/v1/files/${item.id}/content`);
        window.open(contentHolder.data.link)
      }
    },
    async fetchCurrentFolderContent() {
      if (this.currentDirectory === null) {
        return;
      }
      this.loading = true;
      this.currentItems = [];
      const contentResponse = await axios.get(
        `be/v1/files/${this.currentDirectory}`
      );
      this.loading = false;
      if (!contentResponse.data.children) {
        throw "Invalid structure received";
      }
      this.currentItems = contentResponse.data.children;
    },
    async deleteItem(item) {
      // todo confirm dialog
      this.loading = true;
      await axios.delete(`be/v1/files/${item.id}`);
      this.loading = false;
      this.fetchCurrentFolderContent();
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
  flex-wrap: wrap;
  padding: 1rem;
}

.no-content {
  flex-grow: 1;
  color: gray;
  padding: 1rem;
}

.main-content {
  display: flex;
  justify-content: center;
}

.items-container {
  max-width: 50%;
  flex-grow: 1;
}

.item {
  display: flex;
  justify-content: space-between;
}

.item-icon {
  margin-right: 0.5rem;
}

.link {
  color: gray;
  cursor: pointer;
}

.link:hover {
  color: black;
  text-decoration: underline !important;
}

.delete-icon {
  margin-right: 0.5rem;
}

.delete-icon:hover {
  color: red;
  cursor: pointer;
}
</style>