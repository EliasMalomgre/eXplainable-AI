<template>
  <div id="analysissection" class="q-pa-lg">
    <div class="q-pa-lg container">
      <h3 class="text-primary q-ma-none">Explanation: {{ bundle.name }}</h3>
      <div class="text-caption">Fill in the form to request an explanation</div>
      <div class="text-caption text-negative q-pt-sm q-pl-lg">* Required fields</div>
    </div>
    <explanation-request :bundleId="id" :dataType="bundle.requiredDataType" :name="bundle.name"
                         :labels="bundle.classLabels"/>
  </div>
</template>

<script>
import ExplanationRequest from '@/components/forms/ExplanationRequest'

export default {
  name: 'Analysis',
  components: {
    ExplanationRequest
  },
  data() {
    return {
      id: this.$route.params.id,
      bundle: this.$route.params.bundle
    }
  },
}
</script>


