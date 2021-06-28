<template>
  <div id="taskssection" class="q-pa-lg">
    <div class="q-pa-lg container">
      <div><h3 class="q-ma-none text-primary">Tasks</h3></div>
      <div class="text-caption">All explanation requests</div>
    </div>
    <overview-task-cards/>
  </div>
</template>

<script>
import OverviewTaskCards from "@/components/cards/tasks/OverviewTaskCards";

export default {
  name: 'Tasks',
  components: {OverviewTaskCards},
  data: () => ({
    tasks: [],
    loading: true,
  }),
}
</script>
