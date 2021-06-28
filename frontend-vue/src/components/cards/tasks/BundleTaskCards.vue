<template>
  <div>
    <div v-if="tasks.length === 0">
      <div class="q-pa-lg" align="center"><i class="text-body2 text-accent">No explanations found for this AI</i></div>
    </div>
    <div v-for="task in tasks" :key="task.id" class="q-py-md q-pl-md row q-gutter-md">
      <q-intersection
          class="window-width"
          once
          transition="scale"
      >
        <q-card flat bordered>
          <q-card-section>
            <div class="row">
              <div class="col-5">
                <div><span class="text-primary">Type: </span>
                  <span>
                <q-chip>{{ task.plotType }}</q-chip>
                <q-chip>{{ task.explainerType }}</q-chip>
                </span>
                </div>
              </div>
              <div class="col-7">
                <div class="row justify-end items-center">
                  <div align="right">

                    <span class="text-caption text-accent q-pl-lg q-pr-sm" align="right">id:{{ task.id }}</span>
                    <q-btn color="negative"
                           unelevated
                           round
                           size="sm"
                           icon="delete"
                           @click="deletedTask = task.id; clearTask = true; ">

                      <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
                        Deletes this explanation result
                      </q-tooltip>

                    </q-btn>
                  </div>
                </div>
              </div>

            </div>
            <div :id=task.id>
              <div class="row items-center">
                <div><span class="text-primary">Status: </span>
                  <status-chip :status="task.status" :errors="task.errors"/>
                </div>
              </div>

              <div class="row items-center">

                <div class="col-xs-6 col-sm-6 col-md-3 q-pb-sm q-pr-md"
                     v-if="task.status === 'PENDING' || task.status === 'CANCELLED'">
                  <div class="text-primary">Time scheduled:</div>
                  {{ humanizedDate((task.timeScheduled)) }}
                </div>

                <div class="col-xs-6 col-sm-6 col-md-3 q-pb-sm q-pr-md"
                     v-if="task.status === 'INPROGRESS'">
                  <div class="text-primary">Time started:</div>
                  {{ humanizedDate((task.timeStarted)) }}
                </div>

                <div class="col-xs-6 col-sm-6 col-md-3 q-pb-sm q-pr-md"
                     v-if="task.status === 'DONE'">
                  <div class="text-primary">Time finished:</div>
                  {{ humanizedDate((task.timeDone)) }}
                </div>

                <div class="col-xs-6 col-sm-6 col-md-3 q-pb-sm q-pr-md" v-if="task.status === 'DONE'">
                  <div class="text-primary">Process Duration:</div>
                  {{ transformSeconds(task.processDuration) }}
                </div>

              </div>

              <div v-if="task.status === 'DONE'">
                <q-separator spaced/>

                <q-intersection
                    class="q-pt-sm"
                    once
                    transition="fade"
                    align="center"
                >
                  <!-- Used vh to ensure high/long images remain visible -->
                  <result-carousel :results="task.results" style="max-width: 70vh"/>
                </q-intersection>
              </div>

            </div>
          </q-card-section>

          <q-inner-loading :showing="loading">
            <q-spinner size="50px" color="primary"/>
          </q-inner-loading>

        </q-card>
      </q-intersection>
    </div>
    <q-dialog v-model="clearTask" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="delete" color="negative" text-color="white"/>
          <span class="q-ml-sm">Are you sure you want to delete this task?</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="primary" v-close-popup/>
          <q-btn flat label="Delete" color="negative" v-close-popup @click="deleteTask(deletedTask)"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<style lang="scss" scoped>
.resultImage {
  width: 100%
}
</style>

<script>
import TaskService from '../../../services/taskService.js'
import StatusChip from '@/components/misc/StatusChip'
import ResultCarousel from "@/components/cards/tasks/ResultCarousel";
import moment from "moment";

export default {
  name: 'BundleTaskCards',
  data: () => ({
    imageSrc: "http://localhost:7080/api/result/getResult?resultId=",
    tasks: [],
    loading: true,
    timer: '',
    clearTask: false,
    deletedTask: null,
  }),
  props: {
    'bundleId': {
      type: String
    }
  },
  components: {
    ResultCarousel,
    StatusChip,
  },
  methods: {
    async getTasksForUpload() {
      await TaskService.getTasksForUpload(this.bundleId).then((data) => this.tasks = data).then(() => {
        this.loading = false;
      });
    },
    cancelAutoUpdate() {
      clearInterval(this.timer);
    },
    async deleteTask(id) {
      await TaskService.deleteTask(id).then(() => this.getTasksForUpload())
    },
    transformSeconds(seconds) {
      var date = new Date(null);
      date.setSeconds(seconds);
      return date.toISOString().substr(11, 8);
    },
    humanizedDate(date) {
      return moment.utc(date).fromNow()
    }
  },
  beforeCreate() {
    this.loading = true;
  },
  created() {
    this.getTasksForUpload();
    this.timer = setInterval(this.getTasksForUpload, 5000);
  },
  beforeDestroy() {
    this.cancelAutoUpdate();
  }
}
</script>

