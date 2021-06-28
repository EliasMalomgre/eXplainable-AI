<template>
  <div>
    <q-carousel
        swipeable
        transition-prev="jump-right"
        transition-next="jump-left"
        animated
        padding
        control-color="primary"
        v-model="slide"
        ref="carousel"
        :fullscreen.sync="fullscreen"
        keep-alive
        :keep-alive-max="10"
        style="height: 100%"
    >
      <q-carousel-slide v-for="(result, index) in results"
                        :key="result.id"
                        :name="index+1"
                        style="min-height: 200px;"
                        class="row items-center justify-center q-pa-none">
        <result-image :result-id="result.id" :zoom="fitScreen"/>
      </q-carousel-slide>

      <template v-slot:control v-if="fullscreen">
        <q-carousel-control
            position="bottom"
            align="center"
        >
          <div class="row items-center justify-center">
            <div class="col-xs-0 col-sm-0 col-md-3"/>
            <div class="col-xs-8 col-sm-8 col-md-6" align="center">
              <div v-show="navigationRequired" class="carousel-fullscreen-controls">
                <q-btn
                    push round flat
                    text-color="white"
                    icon="first_page"
                    @click="$refs.carousel.goTo(1)">
                  <q-tooltip anchor="center left" self="center right" :offset="[0, 0]">
                    Go to first
                  </q-tooltip>
                </q-btn>
                <q-btn
                    push round flat
                    text-color="white"
                    icon="chevron_left"
                    @click="$refs.carousel.previous()">
                  <q-tooltip anchor="center left" self="center right" :offset="[0, 0]" :delay="500">
                    Go to previous
                  </q-tooltip>
                </q-btn>
                <span class="text-white text-body2">
                    {{ slide }} / {{ results.length }}
                  </span>
                <q-btn
                    push round flat
                    text-color="white"
                    icon="chevron_right"
                    @click="$refs.carousel.next()">
                  <q-tooltip anchor="center right" self="center left" :offset="[0, 0]" :delay="500">
                    Go to next
                  </q-tooltip>
                </q-btn>
                <q-btn
                    push round flat
                    text-color="white"
                    icon="last_page"
                    @click="$refs.carousel.goTo(results.length)">
                  <q-tooltip anchor="center right" self="center left" :offset="[0, 0]">
                    Go to last
                  </q-tooltip>
                </q-btn>
              </div>
            </div>
            <div class="col-xs-4 col-sm-4 col-md-3" align="right">
              <q-btn
                  push round flat
                  class="carousel-fullscreen-controls q-ma-xs"
                  text-color="white"
                  :icon="fitScreen ? 'close_fullscreen' : 'open_in_full'"
                  @click="fitScreen = !fitScreen">
                <q-tooltip anchor="center right" self="center left" :offset="[0, 0]">
                  <span v-show="!fitScreen">Fit Screen</span>
                  <span v-show="fitScreen">Zoom Out</span>
                </q-tooltip>
              </q-btn>
              <q-btn
                  push round flat
                  class="carousel-fullscreen-controls q-ma-xs"
                  text-color="white"
                  :icon="fullscreen ? 'fullscreen_exit' : 'fullscreen'"
                  @click="toggleFullscreen">
                <q-tooltip anchor="center left" self="center right" :offset="[0, 0]">
                  Close Fullscreen
                </q-tooltip>
              </q-btn>
            </div>
          </div>
        </q-carousel-control>
      </template>
    </q-carousel>

    <div class="row items-center justify-center">
      <div class="col-1"/>
      <div class="col-10" align="center">
        <div v-show="navigationRequired">
          <q-btn
              push round flat
              text-color="primary"
              icon="first_page"
              @click="$refs.carousel.goTo(1)">
            <q-tooltip anchor="center left" self="center right" :offset="[0, 0]">
              Go to first
            </q-tooltip>
          </q-btn>
          <q-btn
              push round flat
              text-color="primary"
              icon="chevron_left"
              @click="$refs.carousel.previous()">
            <q-tooltip anchor="center left" self="center right" :offset="[0, 0]" :delay="500">
              Go to previous
            </q-tooltip>
          </q-btn>
          <span class="text-primary text-body2">
            {{ slide }} / {{ results.length }}
          </span>
          <q-btn
              push round flat
              text-color="primary"
              icon="chevron_right"
              @click="$refs.carousel.next()">
            <q-tooltip anchor="center right" self="center left" :offset="[0, 0]" :delay="500">
              Go to next
            </q-tooltip>
          </q-btn>
          <q-btn
              push round flat
              text-color="primary"
              icon="last_page"
              @click="$refs.carousel.goTo(results.length)">
            <q-tooltip anchor="center right" self="center left" :offset="[0, 0]">
              Go to last
            </q-tooltip>
          </q-btn>
        </div>
      </div>
      <div class="col-1" align="right">
        <q-btn
            push round flat
            text-color="primary"
            :icon="fullscreen ? 'fullscreen_exit' : 'fullscreen'"
            @click="toggleFullscreen">
          <q-tooltip anchor="center left" self="center right" :offset="[0, 0]">
            Fullscreen
          </q-tooltip>
        </q-btn>
      </div>
    </div>

  </div>
</template>

<style lang="sass" scoped>
.carousel-fullscreen-controls
  text-align: center
  padding: 12px
  color: white
  background-color: rgba(0, 0, 0, .3)
  border-radius: 100px
</style>

<script>
import ResultImage from "@/components/cards/tasks/ResultImage";

export default {
  name: 'ResultCarousel',
  data: () => ({
    imageSrc: "http://localhost:7080/api/result/getResult?resultId=",
    tasks: [],
    loading: true,
    timer: '',
    clearTask: false,
    slide: 1,
    fullscreen: false,
    fitScreen: false,
  }),
  props: {
    results: null
  },
  computed: {
    navigationRequired() {
      if (this.results.length === 1) {
        return false;
      } else {
        return true;
      }
    }
  },
  methods: {
    toggleFullscreen() {
      if (this.fullscreen) {
        this.fitScreen = false;
      }
      this.fullscreen = !this.fullscreen;
    }
  },
  components: {
    ResultImage,
  },
}
</script>

