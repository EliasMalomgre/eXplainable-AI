import Vue from 'vue'
import App from './App.vue'
import moment from 'moment';
import './registerServiceWorker'
import router from './router'
import './quasar'
import './quasar'
import './quasar'

Vue.config.productionTip = false

Vue.filter('formatDate', function(value: string) {
  if (value) {
    return moment(String(value)).local().format('DD/MM/YYYY HH:mm:ss ')
  }
});

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
