<template>
  <span>
    <q-chip v-if="status === 'PENDING'" icon="schedule" color="secondary" text-color="white">
      {{ status }}
      <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
        Task is waiting in the queue
      </q-tooltip>
    </q-chip>
    <q-chip v-if="status === 'INPROGRESS'" icon="cached" color="primary" text-color="white">
      {{ status }}
      <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
        Task is currently being executed
      </q-tooltip>
    </q-chip>
    <q-chip v-if="status === 'DONE'" icon="done" color="positive" text-color="white">
      {{ status }}
      <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
        Task has finished successfully
      </q-tooltip>
    </q-chip>
    <q-chip v-if="status === 'CANCELLED'" icon="fas fa-times" color="negative" text-color="white">
      {{ status }}
      <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
        The task has been cancelled
      </q-tooltip>
    </q-chip>
    <span v-if="errors !== undefined && status !== 'PENDING'">
      <span v-if="status !== 'CANCELLED'">
        <q-chip v-if="!errors.includes('CPU_SWITCH')" color="positive" text-color="white">
          GPU
          <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
            Explanation <span v-if="status === 'INPROGRESS'">is being</span><span v-else>was</span> executed using GPU acceleration
          </q-tooltip>
        </q-chip>
        <q-chip v-else color="warning" text-color="white">
          CPU
          <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
            The GPU could not be utilised, switched over to slower CPU processing
          </q-tooltip>
        </q-chip>
      </span>
      <span v-else>
        <q-chip v-if="!errors.includes('CPU_FAILURE')" color="negative" text-color="white">
          CPU
          <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
            Both GPU and CPU utilisation resulted in errors, cancelled task!
          </q-tooltip>
        </q-chip>
        <q-chip v-else color="negative" text-color="white">
          ERROR
          <q-tooltip anchor="bottom middle" self="top middle" :offset="[10, 10]">
            An internal server error occurred, preventing execution of this task!
          </q-tooltip>
        </q-chip>
      </span>
    </span>

  </span>
</template>

<script>
export default {
  name: "StatusChip",
  props: {
    status,
    errors: null,
  },
}

</script>

<style scoped>

</style>