<template>
  <div id="bundlesection" class="q-pa-lg container">

    <div class="row items-center justify-between">
      <div class="col-lg-7 col-sm-8 col-xs-12 q-pa-lg">
        <div><h3 class="q-ma-none text-primary ellipsis-2-lines">{{ bundleName }}</h3></div>
        <div class="text-caption text-accent q-pl-lg">id:{{ bundleId }}</div>
      </div>
      <div class="col-lg-5 col-sm-4 col-xs-12">
        <div class="row justify-end q-pr-lg" align="right">
          <div class="q-pb-sm col-lg-7 col-md-12">
            <q-btn color="primary"
                   @click="requestExplanation"
                   label="Request explanation"
                   icon-right="send"
                   size="sm"
                   unelevated/>
          </div>
          <div class=" q-pl-sm col-lg-5 col-md-12" align="right">
            <q-btn label="Delete bundle"
                   color="negative"
                   unelevated
                   icon-right="delete_forever"
                   size="sm"
                   @click="deleteUpload = true">
              <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
                Deletes this bundle and all its associated data
              </q-tooltip>
            </q-btn>
            <q-dialog v-model="deleteUpload" persistent>
              <q-card>
                <q-card-section class="row items-center">
                  <q-avatar icon="delete_forever" color="negative" text-color="white"/>
                  <span class="q-ml-sm">Are you sure you want to delete this AiBundle?</span>
                </q-card-section>

                <q-card-actions align="right">
                  <q-btn flat label="Cancel" color="primary" v-close-popup/>
                  <q-btn flat label="Delete" color="negative" v-close-popup @click="deleteBundle"/>
                </q-card-actions>
              </q-card>
            </q-dialog>
          </div>
        </div>
      </div>
    </div>

    <q-separator spaced inset/>

    <div v-if="bundle != null">
      <div ref="bundleDetails" class="row">

        <div class="q-pa-lg col-lg-5 col-md-7 col-sm-12" v-if="bundle.description != null">
          <div><h5 class="q-ma-none text-primary">Description</h5></div>
          <div class="q-ml-lg bundle-description">
            {{ bundle.description }}
          </div>
        </div>

        <div class="q-pa-lg col-lg-7 col-md-5 col-sm-12">
          <div><h5 class="q-ma-none text-primary">Details</h5></div>
          <div class="q-ml-lg">

            <div>
              <span class="text-primary">Datatype: </span>{{ bundle.requiredDataType }}
            </div>

            <div>
              <span class="text-primary">Inputshape: </span>{{ bundle.inputShape }}
            </div>

            <div v-if="bundle.classLabels.length > 0">
              <span class="text-primary">Class labels ({{ bundle.classLabels.length }}): </span>
              <div>
                <q-scroll-area
                    class="row"
                    :delay="1200"
                    :thumb-style="thumbStyle"
                    :bar-style="barStyle"
                    style="height: 90px; width: 100%"
                >
                <span v-for="(label, index) in bundle.classLabels" :key="index">
                  <q-chip class="q-mr-xs q-mt-none q-ml-none q-mb-xs q-pa-sm" color="primary" text-color="white">
                    {{ label }}
                  </q-chip>
                </span>
                </q-scroll-area>
              </div>
            </div>

            <div class="q-mt-sm">
              <span class="text-primary">Time uploaded: </span>{{ bundle.timeUploaded | formatDate }}
            </div>

            <div>
              <span class="text-primary">Last Activity: </span>{{ bundle.lastActivity | formatDate }}
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-sm-12 q-pt-lg q-pr-md q-mb-md q-pl-lg">
          <h5 class="q-ma-none text-primary">Visualisation Structure
            <q-toggle
                :label="`${onOffMessage}`"
                color="primary"
                v-model="visualisationEnabled"
                align="right"
            >
              <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
                Toggles the visualisation, can take several seconds
              </q-tooltip>
            </q-toggle>
          </h5>

          <div>
            <Scene :id="bundleId" :neuralNet="bundle.deserializedNeuralNet"
                   :inputShape="bundle.inputShape" :labels="bundle.classLabels"
                   v-if="visualisationEnabled"/>
          </div>
        </div>
      </div>
    </div>

    <q-separator spaced inset/>

    <div class="q-pa-lg">
      <div class="row">
        <div class="col-8">
          <div><h5 class="q-ma-none text-primary">Explanation Tasks</h5></div>
          <div class="text-caption">All explanations requested for this AI</div>
        </div>
        <div class="col-4" align="right">
          <q-btn label="Clear all"
                 color="negative"
                 unelevated
                 icon-right="delete_forever"
                 size="sm"
                 @click="deleteTasks = true">
            <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
              Deletes all explanations for this bundle
            </q-tooltip>
          </q-btn>
          <q-dialog v-model="deleteTasks" persistent>
            <q-card>
              <q-card-section class="row items-center">
                <q-avatar icon="delete_forever" color="negative" text-color="white"/>
                <span class="q-ml-sm">Are you sure you want to clear all tasks?</span>
              </q-card-section>

              <q-card-actions align="right">
                <q-btn flat label="Cancel" color="primary" v-close-popup/>
                <q-btn flat label="Delete" color="negative" v-close-popup @click="deleteTasksForAIBundle"/>
              </q-card-actions>
            </q-card>
          </q-dialog>
        </div>
      </div>
      <div>
      </div>
      <task-cards v-bind:bundle-id="bundleId" :key="bundleId"/>
    </div>

  </div>
</template>

<script>
import AiBundleService from "../services/aiBundleService"
import TaskService from "../services/taskService"
import TaskCards from "@/components/cards/tasks/BundleTaskCards"
import AIVisualisation from "@/components/webgl/AIVisualisation"
import {EventBus} from '@/eventBus';
import {colors} from 'quasar'

const {getPaletteColor} = colors

export default {
  name: "Bundle",
  data: () => ({
    bundle: null,

    bundleId: null,
    bundleName: null,
    deleteUpload: false,
    deleteTasks: false,

    visualisationEnabled: false,
    thumbStyle: {
      right: '4px',
      borderRadius: '5px',
      backgroundColor: getPaletteColor('primary'),
      width: '5px',
      opacity: 0.75
    },
    barStyle: {
      right: '2px',
      borderRadius: '9px',
      backgroundColor: getPaletteColor('secondary'),
      width: '9px',
      opacity: 0.1
    }
  }),
  watch: {
    $route() {
      this.visualisationEnabled = false;
      this.bundleId = this.$route.params.id;
      this.getBundle()
    }
  },
  computed: {
    height() {
      return this.$refs.bundleDetails.clientHeight;
    },
    onOffMessage() {
      if (this.visualisationEnabled) {
        return "On"
      } else {
        return "Off"
      }
    }
  },
  methods: {
    requestExplanation() {
      this.$router.push({name: 'Analysis', params: {id: this.bundleId, bundle: this.bundle}})
    },
    async getBundle() {
      await AiBundleService.getAIBundle(this.bundleId).then(
          (data) => {
            this.bundle = data;
            this.bundleName = this.bundle.name;
          }
      )
    },
    async deleteBundle() {
      await AiBundleService.deleteAIBundle(this.bundleId).then(
          () => {
            EventBus.$emit('reloadUploads');
            this.$router.push({name: 'Home'});
          }
      )
    },
    async deleteTasksForAIBundle() {
      await TaskService.deleteTasksForAIBundle(this.bundleId);
    },
  },
  components: {
    TaskCards,
    Scene: AIVisualisation,
  },
  created() {
    this.bundleId = this.$route.params.id;
    this.getBundle(this.bundleId);
  }
  ,
}

</script>

<style scoped>
.bundle-description {
  white-space: normal;
  overflow: hidden;
  text-overflow: ellipsis;
}

</style>