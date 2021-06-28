<template>
  <div>
    <div class="text-center q-my-none q-py-none">
      <h5 class="q-my-sm text-primary">
        <q-icon name="psychology" style="font-size: 1.2em;" class="text-primary"/>
        - AI's
      </h5>
    </div>

    <div v-if="bundles.length === 0">
      <div class="q-pa-lg" align="center"><i class="text-body2 text-accent">No AI's found</i></div>
    </div>
    <q-list v-else>
      <q-separator/>
      <div v-for="bundle in bundles" :key="bundle.id">
        <q-item v-ripple clickable @click="showUpload(bundle.id)"
                separator :active="active === bundle.id" active-class="text-primary mnu_active"
                class="row items-start" flat bordered>
          <q-item-section>
            <div class="row">
              <div class="q-pl-sm">
                <h6 class="q-mb-sm q-mt-none">{{ bundle.name }}</h6>
                <p class="q-my-none">Uploaded: {{ bundle.timeUploaded | formatDate }}</p>
                <p class="q-my-none">Last activity: {{ bundle.lastActivity | formatDate }}</p>
              </div>
            </div>
          </q-item-section>
        </q-item>
        <q-separator/>
      </div>

    </q-list>
  </div>
</template>

<style>

</style>

<script>
import AiBundleService from "../../services/aiBundleService.js"
import {EventBus} from "@/eventBus";

export default {
  name: "BundleCards",
  data: () => ({
    bundles: [],
    active: null,
    timer: ''
  }),
  methods: {

    showUpload(id) {
      this.active = id
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      this.$router.push({name: 'Bundle', params: {id: id}}).catch(err => {})
    },

    async getAIBundles() {
      await AiBundleService.getAIBundles().then((data) => this.bundles = data);
    },

    reloadHandler() {
      this.getAIBundles()
    },

    cancelAutoUpdate () {
      clearInterval(this.timer);
    }
  },
  created() {
    this.getAIBundles()

    EventBus.$on('reloadUploads', this.reloadHandler)
    this.timer = setInterval(this.getAIBundles, 60000)
  },

  beforeDestroy () {
    this.cancelAutoUpdate();
  }
}
</script>