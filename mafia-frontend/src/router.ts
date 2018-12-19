import Vue from 'vue';
import Router from 'vue-router';
import Start from './views/Start.vue';
import Game from './views/Game.vue';

Vue.use(Router);

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      redirect: {
        name: 'start',
      },
    },
    {
      path: '/game/start',
      name: 'start',
      component: Start,
    },
    {
      path: '/game',
      name: 'game',
      component: Game,
    },
  ],
});
