<template>
  <div>
    <div v-if="tasks.length === 0">
      <div class="q-pa-lg" align="center"><i class="text-body2 text-accent">No previous tasks found</i></div>
    </div>
    <div v-else v-for="task in tasks" :key="task.id" class="q-pa-xs container">
      <q-card :id=task.id flat bordered>
        <q-card-section class="q-pb-xs">
          <div class="row justify-between">
            <div class="col-6">
              <span class="text-primary">Type: </span>
              <q-chip>{{ task.plotType }}</q-chip>
              <q-chip>{{ task.explainerType }}</q-chip>
            </div>
            <div class="col-6" align="right">
              <status-chip :status="task.status" :errors="task.errors"/>

              <q-btn class="q-ml-sm" size="sm" color="primary" round unelevated icon="launch"
                     @click="showUpload(task.aiBundleId)">
                <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
                  Go to respective bundle
                </q-tooltip>
              </q-btn>
            </div>
          </div>

          <div class="row content-end">

            <div class="col-8">

              <div>
                <span class="text-primary">AI name: </span>{{ task.aiBundleName }}
              </div>

              <div>
                <span class="text-primary">Time scheduled: </span>{{ task.timeScheduled | formatDate }}
              </div>
              <div v-if="task.status === 'DONE' || task.status === 'INPROGRESS'">
                <span class="text-primary">Time started: </span>{{ task.timeStarted | formatDate }}
              </div>
              <div v-if="task.status === 'DONE'">
                <span class="text-primary">Time done: </span>{{ task.timeDone | formatDate }}
              </div>
            </div>
            <div class="col-4" align="right">
              <q-btn v-if="task.status === 'PENDING' || task.status === 'INPROGRESS'" @click="cancelTask(task.id)"
                     label="Cancel" color="negative" class="q-ma-xs" unelevated/>
              <q-btn v-if="task.status === 'CANCELLED'" @click="retryTask(task.id)"
                     label="Retry" color="positive" class="q-ma-xs" unelevated/>
            </div>

          </div>
        </q-card-section>

        <div class="row content-end">
          <div class="col-12">
            <p class="text-caption text-accent q-pr-md" align="right">Task id: {{ task.id }}</p>
          </div>
        </div>

        <q-inner-loading :showing="loading">
          <q-spinner size="50px" color="primary"/>
        </q-inner-loading>

      </q-card>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.resultImage {
  width: 100%
}

.card-container {
  margin-left: auto;
  margin-right: auto;
  width: 100%;
  max-width: 1000px;
}
</style>

<script>
import TaskService from '../../../services/taskService.js'
import StatusChip from '@/components/misc/StatusChip'
import moment from "moment";

export default {
  name: 'OverviewTaskCards',
  data: () => ({
    tasks: [],
    timer: '',
    loading: true,
  }),
  props: {
    'bundleId': {
      type: String
    }
  },
  components: {
    StatusChip,
  },
  methods: {
    async getAllTasks() {
      await TaskService.getAllTasks().then((data) => this.tasks = data).then(() => this.loading = false);
    },
    async cancelTask(id) {
      await TaskService.cancelTask(id).then(window.location.reload());
    },
    async retryTask(id) {
      this.$q.notify({
        type: 'info',
        message: `Not yet implemented`,
        position: 'bottom-right',
      })
    },
    showUpload(id) {
      this.$router.push({name: 'Bundle', params: {id: id}})
    },
    cancelAutoUpdate() {
      clearInterval(this.timer);
    },
    humanizedDate(date) {
      return moment.utc(date).fromNow()
    }
  },
  beforeCreate() {
    this.loading = true;
  },
  created() {
    this.getAllTasks();
    this.timer = setInterval(this.getAllTasks, 5000);
  },
  beforeDestroy() {
    this.cancelAutoUpdate();
  }
}
</script>