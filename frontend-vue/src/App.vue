<template>
  <div>
    <Layout/>
  </div>
</template>

<script>
import Layout from '@/layouts/Layout'

export default {
  name: 'App',
  components: {
    Layout,
  },
  beforeCreate() {
    this.$q.loading.show()
  },
  created() {
    //TODO do something with this perhaps? In other components when we're waiting for java
    setTimeout( () => {
      this.$q.loading.hide()
    }, 500)
  },
}
</script>

<style>
</style>
