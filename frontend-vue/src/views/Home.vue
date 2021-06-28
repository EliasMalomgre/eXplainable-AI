<template>
  <div id="homesection">

    <div class="header"/>

    <q-avatar rounded size="30vh" class="splash-logo">
      <img src="../assets/images/logos/xaii-white.svg" alt="logo">
    </q-avatar>

    <div id="splash" class="q-mt-lg splash">
      <div class="row content-center justify-center">
        <h1 class="text-white q-mb-none q-pb-none">XAII</h1>
      </div>

      <div class="row content-center justify-center">
        <h6 class="text-white q-mt-none">Your AI assistant</h6>
      </div>

      <q-space class="q-pa-lg"/>

      <div class="row justify-center container">

        <q-btn color="primary"
               class="col-xs-8 col-md-4 col-lg-4 q-ma-sm"
               @click="toggleDrawer"
               label="check your AI's"
               unelevated
               rounded
        />
        <q-btn color="accent"
               class="col-xs-8 col-md-4 col-lg-4 q-ma-sm"
               @click="routeToUpload"
               label="upload your AI"
               icon-right="upload"
               unelevated
               rounded
        />

        <q-btn color="primary"
               class="col-xs-8 col-md-4 col-lg-4 q-ma-sm"
               @click="routeToAbout"
               label="read more"
               unelevated
               outline
               rounded
        />

      </div>
    </div>
  </div>
</template>

<script>
import {EventBus} from "@/eventBus";

export default {
  name: 'Home',
  methods: {
    routeToUpload() {
      this.$router.push({name: 'Upload'})
    },
    routeToAbout() {
      this.$router.push({name: 'About'})
    },
    toggleDrawer() {
      EventBus.$emit('toggleLeftDrawer')
    },
  },
  created() {
    EventBus.$emit('enterHome');
    EventBus.$emit('closeLeftDrawer');
  },
  beforeDestroy() {
    EventBus.$emit('leaveHome');
  },
}
</script>

<style>
html {
  scroll-behavior: smooth;
}

a {
  text-decoration: none;
}

.splash-logo {
  will-change: transform;
  left: 50%;
  -webkit-transform: translate(-50%);
  transform: translate(-50%);
  top: 50px;
  z-index: 1;
  -webkit-animation: float 7s ease-in-out infinite;
  animation: float 7s ease-in-out infinite;
}

@-webkit-keyframes float {
  50% {
    -webkit-transform: translate(-50%, -10px);
    transform: translate(-50%, -10px);
  }
}

@keyframes float {
  50% {
    -webkit-transform: translate(-50%, -10px);
    transform: translate(-50%, -10px);
  }
}

.splash {
  margin-bottom: 10em;
}

</style>
