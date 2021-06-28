<template>
  <div>
    <div class="q-pa-lg container">

      <q-form
          @submit="submitRequest"
          @reset="onReset"
          class="q-gutter-md"
      >

        <div id="id-section">
          <div class="row items-start justify-start q-pb-lg">
            <div class="col-lg-3 col-md-5 col-xs-12 q-pb-lg">
              <div class="text-h6">Identifier</div>
              <div class="text-caption">Id of the selected Artificial Intelligence</div>
            </div>

            <div class="col-lg-9 col-md-7 col-xs-12">
              <div>
                <q-input class="q-pl-md q-pr-md q-pt-none"
                         v-model="bundleId"
                         label="AI Bundle Id"
                         outlined
                         :disable="true"
                />
              </div>
            </div>
          </div>
        </div>

        <q-space/>

        <div id="datatype-section">
          <div class="row items-start justify-start q-pb-lg">
            <div class="col-lg-3 col-md-5 col-xs-12 q-pb-lg">
              <div class="text-h6">Data type</div>
              <div class="text-caption">The type of the data you're uploading</div>
            </div>

            <div class="col-lg-9 col-md-7 col-xs-12">
              <q-input class="q-pl-md q-pr-md q-pt-none"
                       outlined
                       v-model="dataType"
                       label="Data type of the selected AI"
                       :disable="true"
              />
            </div>
          </div>
        </div>

        <q-space/>

        <div id="explainer-section" class="q-pb-md">
          <div>
            <div class="text-h6">Explainer type<span class="text-negative">*</span></div>
            <div class="text-caption">There are several types of explainer we can use to generate an explanation</div>
          </div>
          <div class="q-px-md">
            <q-option-group
                v-model="taskType"
                :options="taskTypeOptions"
                color="primary"
                label="Pick an explainer"
                align="center"
                inline
            />
          </div>
          <q-card flat bordered class="q-pa-md" v-show="taskType !== null">
            <explainer-info :task-type="taskType" class="text-caption"/>
          </q-card>
        </div>

        <q-space/>

        <div id="plot-section" class="q-pb-md">
          <div>
            <div class="text-h6">Plot type<span class="text-negative">*</span></div>
            <div class="text-caption" v-if="dataType!=='image'">
              On what kind of plot do you wish to visualize the explanation
            </div>
          </div>
          <div class="q-px-md">
            <q-option-group
                v-model="plotType"
                :options="plotOptions"
                v-if="dataType!=='image'"
                color="primary"
                label="Pick a plot"
                align="center"
                inline
            />
          </div>
          <div class="q-mb-xs"/>
          <q-card flat bordered class="q-pa-md">
            <plot-info :plot-type="plotType" class="text-caption"/>
          </q-card>

        </div>

        <q-space/>

        <div id="data-section">
          <div>
            <div class="text-h6">Data to explain<span class="text-negative">*</span></div>
            <div class="text-caption">The data you wish to generate explanations for</div>
          </div>
          <div class="q-pa-md">
            <div v-if="dataType === 'image'" class="q-mb-md">
              <div v-if="data != null && data.length>5">
                <span class="text-warning">WARNING: </span>Uploading a lot of pictures will lead to a high process
                duration and a plot with a large height. We recommend small batch sizes.
              </div>
              <div v-if="data != null && data.length>2">
                <span class="text-positive">RECOMMENDED: </span>We recommend to utilise the 'normalise'-feature, to
                properly display heatmaps per picture.
              </div>
            </div>
            <q-file
                v-model=data
                label="Pick data"
                outlined
                counter
                :counter-label="counterLabelFn"
                use-chips
                multiple
                :filter="checkDataType"
                @rejected="onRejected"
            >
              <template v-slot:prepend>
                <q-icon name="attach_file"/>
              </template>
            </q-file>
          </div>
        </div>

        <div id="ranked-results-section" v-if="labels.length > 0 && dataType==='image'">
          <q-space/>
          <div>
            <div class="text-h6">Number of ranked explanations</div>
            <div class="text-caption">This will rank all explanations and ony show the amount. Submit 0 if you want to
              see all of them not ranked
            </div>
          </div>
          <div class="q-pa-md">
            <div v-if="outputs>10">
              <span class="text-warning">WARNING: </span>We recommend to keep this value small, to reduce process
              duration and the width of plots.
            </div>
            <div class="q-my-md row">
              <q-slider
                  v-model="outputs"
                  :min="0"
                  :max="labels.length"
                  label
                  label-always
                  color="primary"
                  class="q-py-sm col-xs-9 col-sm-10"
              />
              <span class="q-pl-lg col-xs-3 col-sm-2">
              <q-input
                  v-model.number="outputs"
                  type="number"
                  :min="0"
                  :max="labels.length"
                  :rules="[ val => (val <= labels.length && val >= 0) || 'Please respect the allowed range' ]"
                  outlined
              />
                </span>
            </div>
          </div>
        </div>

        <q-space/>

        <div id="normalise-section" v-if="dataType==='image'">
          <div class="row items-center justify-start q-pb-lg">
            <div class="col-lg-10 col-xs-9 q-pb-lg">
              <div class="text-h6">Normalize data</div>
              <div class="text-caption">Shap values will be normalized so the result will be clearer.
                If you want to know to exact shap values you should turn this setting off
              </div>
            </div>
            <div class="col-lg-2 col-xs-3" align="right">
              <div>
                <h6 class="q-ma-none q-ml-sm text-primary">
                  <q-toggle
                      :label="`${onOffMessage}`"
                      color="primary"
                      v-model="normalize"
                      align="right"
                  />
                </h6>
              </div>
            </div>
          </div>
        </div>

        <div class="q-pa-lg" align="right">
          <q-btn
              label="Start explanation"
              type="submit"
              color="primary"
              unelevated
              :loading="submitting"
              :disabled="!(acceptData && bundleId!==null && taskType!==null && dataType != null)"
          >
            <template v-slot:loading>
              <q-spinner-facebook/>
            </template>
          </q-btn>
          <q-btn label="Reset" type="reset" color="primary" unelevated flat class="q-ml-sm"/>
        </div>

      </q-form>
    </div>
  </div>
</template>

<script>
import UploadService from "@/services/uploadService";
import ExplainerInfo from "@/components/misc/ExplainerInfo";
import PlotInfo from "@/components/misc/PlotIInfo";

export default {
  data() {
    return {
      // bundleId: null,
      taskType: null,
      plotType: null,
      data: null,
      outputs: 0,
      normalize: true,

      acceptData: false,
      submitting: false,

      dataTypeOptions: ['IMAGE', 'CSV', 'TEXT'],

      taskTypeImageOptions: [
        {
          label: 'Gradient Explainer (SHAP)',
          value: 'GRADIENT',
        },
        {
          label: 'Deep Explainer (SHAP)',
          value: 'DEEP',
        },
      ],
      taskTypeCSVOptions: [
        {
          label: 'Deep Explainer (SHAP)',
          value: 'DEEP',
        },
        {
          label: 'Kernel Explainer (SHAP)',
          value: 'KERNEL',
        },
      ],
      taskTypeTextOptions: [
        {
          label: 'Kernel Explainer (SHAP)',
          value: 'KERNEL',
          disable: true,
        },
      ],

      taskTypeOptions: null,

      plotOptions: [
        {
          label: 'Decision Plot (SHAP)',
          value: 'DECISION',
        },
        {
          label: 'Embedding Plot (SHAP)',
          value: 'EMBEDDING',
        },
        {
          label: 'Heatmap Plot (SHAP)',
          value: 'HEATMAP',
        },
        {
          label: 'Scatter Plot (SHAP)',
          value: 'SCATTER',
        },
        {
          label: 'Summary Plot (SHAP)',
          value: 'SUMMARY',
        },
        {
          label: 'Waterfall Plot (SHAP)',
          value: 'WATERFALL',
        },
      ],

      // variables for carousel
      slide: null,
    }
  },
  props: {
    bundleId: null,
    dataType: null,
    name: null,
    labels: null
  },
  components: {
    PlotInfo,
    ExplainerInfo
  },
  computed: {
    onOffMessage() {
      if (this.normalize) {
        return "On"
      } else {
        return "Off"
      }
    }
  },
  methods: {
    onReset() {
      this.setInitialValues();
      this.data = null;
      this.outputs = 0;

      this.acceptData = false;
    },
    onFail() {
      this.acceptData = false;
    },
    setInitialValues() {
      const inputDataType = this.dataType.toUpperCase();

      if (inputDataType === 'IMAGE') {
        this.taskTypeOptions = this.taskTypeImageOptions;
        this.taskType = 'GRADIENT';
        this.plotType = 'IMAGE';
      } else if (inputDataType === 'CSV') {
        this.taskTypeOptions = this.taskTypeCSVOptions;
        this.taskType = 'DEEP';
        this.plotType = 'DECISION';
      } else if (inputDataType === 'TEXT') {
        this.taskTypeOptions = this.taskTypeTextOptions;
      } else {
        console.error(`${inputDataType} was not recognized as valid datatype, defaulting to Image options`)
        this.taskTypeOptions = this.taskTypeImageOptions;
      }
    },
    counterLabelFn({totalSize, filesNumber}) {
      return `${filesNumber} files | ${totalSize}`
    },
    async submitRequest() {

      if (this.acceptData
          && this.bundleId !== null
          && this.taskType !== null
          && this.dataType != null
          && this.checkPlotValid(this.plotType)
      ) {

        //turns on the spinner to display to the user we're now processing the data
        this.submitting = true;

        this.$q.notify({
          type: 'info',
          message: `Submission in progress`,
          position: 'top',
          timeout: 3000,
        })

        const sentInUploadId = this.bundleId;
        const sentInTaskType = this.taskType;
        const sentInDataType = this.dataType.toUpperCase();
        const sentInPlotType = this.plotType;
        const sentInData = this.data;
        const outputs = this.outputs;
        const normalize = this.normalize;

        this.onReset();

        const success = await UploadService.requestExplanation(sentInUploadId, sentInTaskType, sentInPlotType, sentInDataType, sentInData, outputs, normalize);
        if (success) {
          this.$q.notify({
            type: 'positive',
            message: `Submission succeeded, request has been added to the queue`,
            position: 'bottom-right',
            timeout: 3000,
          });
          await this.$router.push({name: 'Bundle', params: {id: this.bundleId}});
        } else {
          this.$q.notify({
            type: 'negative',
            message: `Submission failed and was not finalised`,
            position: 'bottom-right',
          })
        }

        // Turns of the spinner, to show the user the submission is done
        this.submitting = false;
      } else {
        this.$q.notify({
          type: 'info',
          message: `Please complete the form first`,
          position: 'top',
        })
        this.submitting = false;
      }
    },
    checkDataType(input) {
      if (input !== null && input !== '') {
        const inputDataType = this.dataType.toUpperCase();

        if (inputDataType === 'IMAGE') {
          //Check if image
          this.acceptData = input.filter(data => data.type === 'image/png' || data.type === 'image/jpeg');
          return this.acceptData;
        } else if (inputDataType === 'CSV') {
          //Check if csv
          this.acceptData = input.filter(data => data.name.split(".").pop() === 'csv');
          return this.acceptData;
        } else if (inputDataType === 'RAWTEXT') {
          //TODO implement a textarea for input text?
          this.acceptData = false;
          return this.acceptData;
        }
        this.acceptData = false;
        return this.acceptData;
      } else {
        //Set data to false if incorrect
        this.acceptData = false;
        return this.acceptData;
      }
    },
    checkPlotValid(plotType) {
      if (this.dataType !== null) {
        const inputDataType = this.dataType.toUpperCase();

        if (inputDataType === 'IMAGE') {
          //Images always have an image plot associated with them
          this.plotType = 'IMAGE';
          return true;
        } else if (inputDataType === 'CSV' && ['DECISION', 'SUMMARY', 'WATERFALL', 'SCATTER', 'HEATMAP', 'EMBEDDING'].includes(plotType)) {
          return true;
        } else {
          return false;
        }

      } else {
        return false;
      }
    },
    onRejected(rejectedEntries) {
      this.$q.notify({
        type: 'negative',
        message: `${rejectedEntries.length} file(s) did not pass validation constraints`,
        position: 'bottom-right',
      })
    }
  },
  created() {
    this.setInitialValues();
  }
}
</script>
