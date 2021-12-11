<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <span class="text-h5">ShareSome</span>
      <v-spacer></v-spacer>
      <span>{{ username }}</span>
    </v-app-bar>

    <v-main>
      <app-login v-if="!loggedIn"></app-login>
      <div v-else>
        <app-main :user="userInfo" @token-saved="tokenSaved"></app-main>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import AppLogin from "./components/AppLogin.vue";
import AppMain from "./components/AppMain.vue";
import axios from "axios";
export default {
  name: "App",

  components: { AppLogin, AppMain },

  data: () => ({
    userInfo: null,
  }),
  created() {
    this.getUserInfo();
  },
  computed: {
    username() {
      return this.userInfo && this.userInfo.username; // todo check why optional chaning is not working
    },
    loggedIn() {
      return this.userInfo !== null;
    }
  },
  methods: {
    async getUserInfo() {
      try {
        const meResponse = await axios.get("/be/v1/users/me");
        this.userInfo = meResponse.data;
      } catch (error) {
        if (error.response && error.response.status === 403) {
          // not logged in
          return;
        } else {
          throw error;
        }
      }
    },
    tokenSaved() {
      this.getUserInfo();
    },
  }
};
</script>
