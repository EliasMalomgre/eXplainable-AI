<template>
  <div>
    <div class="q-pa-lg container">

      <q-form
          @submit="submitFile"
          @reset="onReset"
          class="q-gutter-md"
      >
        <div>
          <div class="text-h6">Name<span class="text-negative">*</span></div>
          <div class="text-caption">Name for artificial intelligence</div>
        </div>

        <div class="q-pa-md">
          <q-input
              v-model="name"
              label="Name"
              hint="Name for artificial intelligence"
              :rules="[
                  val => val !== null && val !== '' || 'Field is required',
                  val => val.length <= 100 || 'Please use a maximum of 100 characters'
                  ]"
              outlined
          >
            <template v-slot:prepend>
              <i class="fas fa-pen"></i>
            </template>
          </q-input>
        </div>

        <div>
          <div class="text-h6">Description</div>
          <div class="text-caption">Description for artificial intelligence</div>
        </div>

        <div class="q-pa-md">
          <q-input
              v-model="description"
              label="Description"
              hint="Description for artificial intelligence"
              type="textarea"
              :rules="[val => (val === null || val.length <= 1500) || 'Please use a maximum of 1500 characters']"
              outlined
          >
            <template v-slot:prepend>
              <i class="fas fa-edit"></i>
            </template>
          </q-input>
        </div>

        <div>
          <div class="text-h6">Model<span class="text-negative">*</span></div>
          <div class="text-caption">Keras h5 files or other types of models converted to ONNX</div>
        </div>

        <div class="q-pa-md">
          <q-file
              v-model=model
              label="Pick a model"
              outlined
              counter
              :counter-label="counterLabelFn"
              :filter="checkModelType"
              @rejected="onRejected"
          >
            <template v-slot:prepend>
              <i class="fas fa-brain"></i>
            </template>
          </q-file>
        </div>

        <q-space/>

        <div>
          <div class="text-h6">Data<span class="text-negative">*</span></div>
          <div class="row">
            <div class="text-caption col-xs-12 col-sm-7 col-lg-5">
              What kind of data does your neural net use as input
            </div>
            <div class="col-xs-12 col-sm-5 col-lg-7 q-px-lg q-pb-lg">
              <q-option-group
                  v-model="dataType"
                  :options="dataTypeOptions"
                  color="primary"
                  align="right"
                  @input="data = null; classLabels = null;"
                  inline
              />
            </div>
          </div>
        </div>


        <q-space/>

        <div id="imageupload-section" v-if="dataType === 'IMAGE'">
          <div>
            <div class="text-h6">Image Training set<span class="text-negative">*</span></div>
            <div class="text-caption">Respect the correct resolution and upload preferably between 100-2000 images</div>
          </div>
          <div class="q-pa-md">
            <q-file
                v-model=data
                label="Pick images"
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

        <q-space/>

        <div id="csvupload-section" v-if="dataType === 'CSV'">
          <div>
            <div class="text-h6">Training set<span class="text-negative">*</span></div>
            <div class="text-caption">Upload the training set as a .csv file, which preferably contains between 100-2000
              entries
            </div>
          </div>
          <div class="q-pa-md">
            <q-file
                v-model=data
                label="Pick data"
                outlined
                counter
                :counter-label="counterLabelFn"
                :filter="checkDataType"
                @rejected="onRejected"
            >
              <template v-slot:prepend>
                <q-icon name="attach_file"/>
              </template>
            </q-file>
          </div>
        </div>

        <q-space/>

        <div id="class-label-section">
          <div>
            <div class="text-h6">Output class labels</div>
            <div class="text-caption">Csv of all class labels separated with a comma</div>
          </div>
          <div class="q-pa-md">
            <q-file
                v-model=classLabels
                label="Pick class labels"
                outlined
                counter
                :counter-label="counterLabelFn"
                :filter="checkLabelType"
                @rejected="onRejected"
            >
              <template v-slot:prepend>
                <q-icon name="attach_file"/>
              </template>
            </q-file>
          </div>
        </div>

        <div class="q-pa-lg" align="right">
          <q-btn
              label="Upload model and dataset"
              type="submit"
              color="primary"
              unelevated
              :loading="submitting"
              :disabled="!(acceptData && acceptModel)"
          >
            <template v-slot:loading>
              <q-spinner-facebook/>
            </template>
          </q-btn>
          <q-btn label="Reset" type="reset" color="primary" flat class="q-ml-sm"/>
        </div>
      </q-form>
    </div>
  </div>
</template>


<script>
import UploadService from "@/services/uploadService";
import {EventBus} from '@/eventBus';

export default {
  data() {
    return {
      name: '',
      description: null,
      model: null,
      data: null,
      dataType: 'IMAGE',
      dataTypeOptions: [
        {
          label: 'Image',
          value: 'IMAGE'
        },
        {
          label: 'CSV',
          value: 'CSV'
        },
      ],
      classLabels: null,

      acceptModel: false,
      acceptData: false,
      acceptLabels: false,
      submitting: false
    }
  },
  methods: {
    onReset() {
      this.name = '';
      this.description = null;
      this.model = null;
      this.data = null;
      this.classLabels = null;
      this.dataType = 'IMAGE';

      this.acceptModel = false;
      this.acceptData = false;
      this.acceptLabels = false;

    },
    counterLabelFn({totalSize, filesNumber}) {
      return `${filesNumber} files | ${totalSize}`
    },
    async submitFile() {

      if (this.acceptModel && this.acceptData && (this.name !== null && this.name !== '')) {

        //turns on the spinner to display to the user we're now processing the data
        this.submitting = true;


        this.$q.notify({
          type: 'info',
          message: `Submission in progress`,
          position: 'top',
          timeout: 3000,
        })

        const sentInName = this.name;
        const sentInDescription = this.description;
        const sentInModel = this.model;
        const sentInData = this.data;
        const sentInLabels = this.classLabels;
        const sentInDataType = this.dataType;

        this.onReset();

        const success = await UploadService.uploadModelAndData(sentInName, sentInDescription, sentInModel, sentInData, sentInLabels, sentInDataType);
        if (success) {
          this.$q.notify({
            type: 'positive',
            message: `Submission succeeded and has been added to your list`,
            position: 'bottom-right',
            timeout: 3000,
          })
          EventBus.$emit('reloadUploads')
          await this.$router.push({name: 'Bundle', params: {id: success.data}});
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
      }
    },
    checkModelType(input) {
      if (input !== null && input !== '') {
        this.acceptModel = input.filter(model => model.name.split(".").pop() === 'h5' || model.name.split(".").pop() === 'onnx');
        return this.acceptModel;
      } else {
        this.acceptModel = false;
      }
      return this.acceptModel;
    },
    checkDataType(input) {
      if (input !== null && input !== '') {
        if (this.dataType === 'IMAGE') {
          //Check if image
          this.acceptData = input.filter(data => data.type === 'image/png' || data.type === 'image/jpeg');
          return this.acceptData;
        } else if (this.dataType === 'CSV') {
          //Check if csv
          this.acceptData = input.filter(data => data.name.split(".").pop() === 'csv');
          return this.acceptData;
        } else if (this.dataType === 'RAWTEXT') {
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
    checkLabelType(input) {
      if (input !== null && input !== '') {
        this.acceptLabels = input.filter(model => model.name.split(".").pop() === 'csv')
        return this.acceptLabels
      } else {
        this.acceptLabels = false;
      }
      return this.acceptLabels
    }
    ,
    onRejected(rejectedEntries) {
      this.$q.notify({
        type: 'negative',
        message: `${rejectedEntries.length} file(s) did not pass validation constraints`,
        position: 'bottom-right',
      })
    }
  }
}
</script>
