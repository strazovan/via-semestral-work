<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <span>ShareSome</span>
      <v-spacer></v-spacer>
      <span>{{ username }}</span>
    </v-app-bar>

    <v-main>
      <app-login v-if="!loggedIn" v-model="loggedIn"></app-login>
      <div v-else>
        <app-main :user="userInfo" @token-saved="tokenSaved"></app-main>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import AppLogin from "./components/AppLogin.vue";
import AppMain from "./components/AppMain.vue";
export default {
  name: "App",

  components: { AppLogin, AppMain },

  data: () => ({
    loggedIn: false, // todo this should be checked by backend call
    userInfo: null,
  }),
  created() {
    this.getUserInfo();
  },
  computed: {
    username() {
      return this.userInfo && this.userInfo.username; // todo check why optional chaning is not working
    },
  },
  methods: {
    getUserInfo() {
      // return null if the user is not logged in, otherwise returns username and token
      // todo call backend
      if (!this.loggedIn) {
        return null;
      }
      this.userInfo = {
        username: "strazovan",
        tokens: {
          gofile: null,
        },
      };
    },
    tokenSaved() {
      this.getUserInfo();
      // todo this is here just for development
      this.userInfo = {
        ...this.userInfo,
        tokens: { ...this.userInfo.tokens, gofile: "test" },
      };
    },
  },
  watch: {
    loggedIn() {
      // todo this is just for prototyping
      this.getUserInfo();
    },
  },
};
</script>
