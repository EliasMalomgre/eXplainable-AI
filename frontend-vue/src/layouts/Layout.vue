<template>
  <q-layout view="hHh LpR fff">
    <q-header bordered class="bg-white text-primary" height-hint="98">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar rounded>
            <img src="../assets/images/logos/xaii.svg" alt="XAII logo">
          </q-avatar>
          <span class="q-pl-sm text-primary">
            XAII
          </span>
          <!--          <q-badge outline color="primary" class="q-pt-xs">-->
          <!--            R-0.1.0-->
          <!--          </q-badge>-->
        </q-toolbar-title>

        <q-tabs align="right" height-hint="98" indicator-color="primary">
          <q-route-tab to="/home" label="Home"/>
          <q-route-tab to="/upload" label="Upload"/>
          <q-route-tab to="/tasks" label="Task Overview"/>
          <q-route-tab class="float-right" to="/about" label="About"/>
        </q-tabs>
      </q-toolbar>
    </q-header>


    <q-drawer v-model="left" side="left" bordered :overlay="onHome">
      <bundle-cards/>
    </q-drawer>

    <q-drawer v-model="right" side="right" overlay bordered>

      <q-list padding>
        <q-item clickable v-ripple v-on:click="toggleDarkMode">
          <q-item-section avatar>
            <q-icon name="lightbulb"/>
          </q-item-section>
          <q-item-section>
            Toggle darkmode
          </q-item-section>
        </q-item>
      </q-list>


    </q-drawer>

    <q-page-container>
      <q-page>
        <router-view/>
        <q-page-sticky position="left" :offset="[-10, 0]">
          <q-btn v-if="!left"
                 round unelevated flat
                 color="accent"
                 icon="chevron_right"
                 @click="openLeftDrawer"
                 size="lg">
            <q-tooltip anchor="center right" self="center left" :offset="[10, 0]">
              Open your AI drawer
            </q-tooltip>
          </q-btn>
          <q-btn v-if="left"
                 round unelevated flat
                 color="accent"
                 icon="chevron_left"
                 @click="closeLeftDrawer"
                 size="lg">
            <q-tooltip anchor="center right" self="center left" :offset="[10, 0]">
              Close your AI drawer
            </q-tooltip>
          </q-btn>
        </q-page-sticky>
      </q-page>

    </q-page-container>

    <q-footer reveal bordered class="bg-dark text-white">
      <q-toolbar align="center">
        <q-btn color="white" dense flat round icon="menu" @click="left = !left"/>
        <q-toolbar-title>
          <q-avatar size="24px" rounded>
            <img alt="" src="../assets/images/logos/xaii-white.svg">
          </q-avatar>
          <span class="q-pl-md text-body2">eXplainable Artificial Intelligence Interface - <a
              href="https://www.infosupport.com/" target="_blank" class="is-link text-white">Info Support</a></span>
        </q-toolbar-title>
        <q-btn color="white" dense flat round icon="menu" @click="right = !right"/>

      </q-toolbar>
    </q-footer>

  </q-layout>
</template>

<script>
import {EventBus} from "@/eventBus";
import BundleCards from "@/components/cards/BundleCards";

export default {
  name: "Layout",
  data() {
    return {
      left: false,
      right: false,
      onHome: true,
    }
  },
  components: {
    BundleCards,
  },
  methods: {
    toggleDarkMode() {
      this.$q.dark.toggle()
    },
    closeLeftDrawer() {
      this.left = false;
    },
    openLeftDrawer() {
      this.left = true;
    },
    toggleLeftDrawer() {
      if (this.left) {
        this.left = false;
      } else {
        this.left = true;
      }
    },
    enterHome() {
      this.onHome = true;
    }
  },
  created() {
    EventBus.$on('enterHome', () => {
      this.onHome = true
    });
    EventBus.$on('leaveHome', () => {
      this.onHome = false;
      //Prevents the overlayed drawer from opening on narrow screens when moving to other page
      if (this.$q.screen.width > 1000) {
        this.openLeftDrawer();
      }
    })
    EventBus.$on('closeLeftDrawer', this.closeLeftDrawer);
    EventBus.$on('openLeftDrawer', this.openLeftDrawer);
    EventBus.$on('toggleLeftDrawer', this.toggleLeftDrawer);
  }
}

</script>

<style scoped>
.is-link {
  text-decoration: none;
}
</style>