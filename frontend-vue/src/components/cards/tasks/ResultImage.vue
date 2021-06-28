<template>
  <div class="col-12" :style="zoomSlideStyle" align="center">
    <q-card flat class="bg-white" :style="zoomImageStyle" >
      <q-img
          :src="imageLink"
          spinner-color="primary"
          class="resultImage"
          loading="lazy"
          ref="resultimage"
          :style="loadImageStyle"
          @load="loading=false;">
        <template v-slot:error>
          <div class="absolute-full flex flex-center text-white">
            Cannot load image
          </div>
        </template>
      </q-img>
    </q-card>
  </div>
</template>

<style lang="scss" scoped>
.resultImage {
  width: 100%;
}
</style>

<script>
import {QImg} from 'quasar'

export default {
  name: 'ResultImage',
  data: () => ({
    imageSrc: "result/getResult?resultId=",
    loading: true,
    height: 200, //TODO see if we can lock height once loaded in, without running into issues while fullscreen loading
  }),
  props: {
    resultId: null,
    zoom: {
      default: false,
      type: Boolean
    }
  },
  computed: {
    imageLink() {
      return `${process.env.VUE_APP_BACKEND}${this.imageSrc}${this.resultId}`;
    },
    loadImageStyle() {
      if (this.loading) {
        return `min-height: ${this.height}px; width: 100%;`
      } else {
        return "width: 100%;"
      }
    },
    zoomImageStyle() {
      if (this.zoom) {
        return ""
      } else {
        return "max-width: 1000px;"
      }
    },
    zoomSlideStyle() {
      if (this.zoom) {
        return ""
      } else {
        return "max-width: 80vw;"
      }
    }
  },
  components: {
    QImg,
  },
}
</script>

