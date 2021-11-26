<template>
  <div class="comment">
    <div class="title">评论</div>
    <div v-if="comments.length">
      <div v-for="(item,index) in comments" :key="item.id">
        <div class="main-comment">
          <div class="username">{{ item.user.username }}</div>
          <div class="content">
            {{ item.content }}
            <span @click="replay(item, index)" class="btn-rep">回复</span>
          </div>
        </div>
        <div class="child-comment" v-for="child in item.allChildren" :key="child.id">
          <div class="username">{{ child.user.username }}</div>
          <div class="content">
            @{{ child.replay.username }} {{ child.content }}
            <span
              @click="replay(child, index)"
              class="btn-rep"
            >回复</span>
          </div>
        </div>
        <div class="replay" v-if="child_comment && cur === index">
          <textarea
            :placeholder="child_placeholder"
            v-model="child_replay_content"
            class="replay-content"
            type="text"
          ></textarea>
          <button @click="replay_comment('comment')" class="replay-btn">回复</button>
        </div>
      </div>
    </div>
    <div class="replay">
      <textarea
        :placeholder="main_placeholder"
        v-model="main_replay_content"
        class="replay-content"
        type="text"
      ></textarea>
      <button @click="replay_comment('media')" class="replay-btn">回复</button>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import { computed, watch, ref, nextTick, onUnmounted, onBeforeUnmount, onActivated } from 'vue'
import { BASR_URL } from '@/assets/js/constant'
import storage from 'good-storage'
import { useRoute } from 'vue-router'
import axios from 'axios'

export default {
  name: 'comment',
  components: {
  },
  setup() {

    // data
    const comments = ref([]);
    const main_replay_content = ref("");
    const child_replay_content = ref("");
    const main_placeholder = ref("发一条友善的评论");
    const child_placeholder = ref("");
    const child_comment = ref(null);
    const cur = ref(null);
    const id = ref(null);
    const user = ref(null);

    // vuex
    const store = useStore()

    // vue-route
    const route = useRoute()

    // console.log(route.query)

    // methods

    async function checkUser() {
      let u = await axios({
        method: "get",
        url: `${BASR_URL}/myself`,
        withCredentials: true,
      })
      console.log(u)
      if (u.data.success) {
        user.value = u.data.data;
      }
    }

    function replay(instance, index) {
      console.log(instance);
      child_comment.value = instance
      cur.value = index
      child_placeholder.value = `回复 @${instance.user.username}:`
    }

    async function replay_comment(type) {
      console.log(type);
      // {
      //   "userId": 5,
      //     "content": "Test",
      //       "parentId": 11,
      //         "type": "comment"
      // }
      let data = null;
      if (type === "media") {
        console.log(id.value, main_replay_content.value)
        data = {
          userId: user.value.id,
          content: main_replay_content.value,
          parentId: id.value,
          type
        }
      } else if (type === "comment") {
        console.log(child_comment.value, child_replay_content.value)
        data = {
          userId: user.value.id,
          content: child_replay_content.value,
          parentId: child_comment.value.id,
          type
        }
      }

      console.log(data)

      let res = await axios({
        method: "post",
        url: `${BASR_URL}/comment/addComment`,
        data,
        headers: { "Content-Type": "application/json", },
        withCredentials: true,
      })
      console.log(res)
      if (res.data.success) {
        let res = await getComments()
        if (res) {
          type === "media" ? main_replay_content.value = "" : (type === "comment" ? child_replay_content.value = "" : null)
        }
      }
    }

    async function getComments() {
      console.log(id.value)
      let result = await axios({
        method: "get",
        url: `${BASR_URL}/comment/getComment?id=${id.value}&type=media`,
        withCredentials: true,
      })

      if (result.data.success) {
        comments.value = result.data.data
        return true
      } else {
        return false
      }
    }

    // hooks

    onActivated(() => {
      comments.value = []
    })

    // computed

    // watch

    onBeforeUnmount(() => {
      unwatch()
    })

    const unwatch = watch(() => route.query, async (newVal, oldVal) => {
      console.log(newVal, oldVal)
      id.value = newVal.id;
      if (newVal.id === undefined) {
        return
      }
      await checkUser()
      let result = await axios({
        method: "get",
        url: `${BASR_URL}/comment/getComment?id=${newVal.id}&type=media`,
        withCredentials: true,
      })

      if (result.data.success) {
        comments.value = result.data.data
      }
      console.log(comments.value)
    }, {
      immediate: true
    })

    // methods
    function goBack() {
      store.commit('setFullScreen', false)
    }

    function error() {
      songReady.value = true
    }


    return {
      goBack,
      error,
      comments,
      replay,
      main_replay_content,
      child_replay_content,
      main_placeholder,
      child_placeholder,
      replay_comment,
      child_comment,
      id,
      cur,
      user,
    }
  }
}
</script>

<style lang="scss" scoped>
textarea::placeholder {
  color: rgba(85, 96, 97, 0.233);
}
.comment {
  background: $color-background;
  position: fixed;
  width: 100%;
  top: 88px;
  bottom: 0;
  overflow: scroll;

  .replay {
    // position: absolute;
    // bottom: 0;
    display: flex;
    width: 100%;
    height: 50px;
    .replay-content {
      flex: 1;
    }
    .replay-btn {
    }
  }

  .title {
    padding: 5px 5px 5px 5px;
  }
  .main-comment {
    background: #888;
    .username {
      padding: 5px 5px 5px 5px;
    }
    .content {
      padding: 5px 5px 5px 5px;
      .btn-rep {
        color: rgb(95, 133, 175);
      }
    }
  }
  .child-comment {
    background: #666;
    padding: 5px 5px 5px 10px;
    .username {
      padding: 5px 5px 5px 5px;
    }
    .content {
      padding: 5px 5px 5px 5px;
      .btn-rep {
        color: rgb(95, 133, 175);
      }
    }
  }
}
</style>
