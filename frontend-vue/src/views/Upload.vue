<template>
  <div id="uploadsection" class="q-pa-lg">
    <div class="q-pa-lg container">
      <h3 class="q-ma-none text-primary">Upload your AI</h3>
      <div class="text-caption">Fill in the form to upload your Artificial Intelligence</div>
      <div class="text-caption text-negative q-pt-sm q-pl-lg">* Required fields</div>
    </div>
    <model-and-data-upload/>
  </div>
</template>

<script>

import ModelAndDataUpload from '@/components/forms/ModelAndDataUpload.vue'

export default {
  name: 'Upload',
  components: {
    ModelAndDataUpload,
  }
}
</script>