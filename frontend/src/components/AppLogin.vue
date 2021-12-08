<template>
  <div class="login">
    <div>
      <h1>Welcome to ShareSome.</h1>
      <div>Please log in.</div>
      <v-btn @click="login">Login</v-btn>
    </div>
  </div>
</template>

<script>
export default {
  name: "AppLogin",
  methods: {
      login() {
          window.location.href = '/be/oauth2/authorization/google'
      }
  }
};
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}
</style>