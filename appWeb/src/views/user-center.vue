<template>
  <div class="user-center">
    <div class="back" @click="back">
      <i class="icon-back"></i>
    </div>
    <div v-if="loged">
      <div class="list-wrapper">
        <scroll class="list-scroll">
          <div class="user-profile">
            <div class="avator" :style="avatorStyle"></div>
            <div class="user-name">{{ user.data.username }}已登录</div>
          </div>
        </scroll>
      </div>
    </div>
    <div v-else>
      <div class="switches-wrapper">
        <switches :items="['账号登录', '账号注册']" v-model="currentIndex"></switches>
      </div>
      <!-- <div class="play-btn" v-if="currentList.length" @click="random">
      <i class="icon-play"></i>
      <span class="text">随机播放全部</span>
      </div>-->
      <div class="list-wrapper">
        <scroll class="list-scroll" v-if="currentIndex === 0">
          <div class="list-inner">
            <div class="search-input-wrapper">
              <all-input v-model="loginData"></all-input>
              <div class="imageCode">
                <img
                  crossorigin
                  ref="loginCode"
                  @click="$refs.loginCode.src = BASR_URL + '/code/image'"
                  class="img"
                  :src="BASR_URL + '/code/image'"
                />
                <input v-model="codes" class="code" placeholder="请输入验证码" />
              </div>
              <button class="input-btn" @click="login">登录</button>
            </div>
          </div>
        </scroll>
        <scroll class="list-scroll" v-if="currentIndex === 1">
          <div class="list-inner">
            <div class="search-input-wrapper">
              <all-input v-model="registerData"></all-input>
              <div class="imageCode">
                <img
                  ref="registerCode"
                  @click="$refs.registerCode.src = BASR_URL + '/code/image'"
                  class="img"
                  :src="BASR_URL + '/code/image'"
                />
                <input v-model="codes" class="code" placeholder="请输入验证码" />
              </div>
              <button class="input-btn">注册</button>
            </div>
          </div>
        </scroll>
      </div>
    </div>
  </div>
</template>

<script>
import Switches from "@/components/base/switches/switches";
import SearchInput from '@/components/search/search-input';
import Scroll from '@/components/wrap-scroll';
import allInput from "@/components/input/allInput"
// import SongList from '@/components/base/song-list/song-list'
import { mapState, mapActions } from "vuex";
import {
  RESOURCE_BASE_URL,
  BASR_URL
} from '@/assets/js/constant'
import axios from 'axios'

export default {
  name: "user-center",
  components: {
    Switches,
    SearchInput,
    Scroll,
    allInput,
    // SongList
  },
  async beforeCreate() {
    // this.checkUser()
    // console.log(this)
  },
  async created() {
    await this.checkUser()
    this.$watch("loginData", (newVal, oldVal) => {
      console.log("loginData", newVal, oldVal)
    })
  },
  data() {
    return {
      BASR_URL: BASR_URL,
      currentIndex: 0,
      loginData: {
        account: "",
        password: ""
      },
      registerData: {
        account: "",
        password: ""
      },
      codes: "",
      loged: false,
      user: {}
    };
  },
  computed: {

    avatorStyle() {
      console.log(this.user.data)
      return {
        'background': this.user.data.avatar ? `url(${RESOURCE_BASE_URL}${this.user.data.avatar})` : '#888',
        "background-size": "contain",
        "background-repeat": "no-repeat"
      }
    }
    // ...mapState(["favoriteList", "playHistory"]),
  },
  methods: {
    async checkUser() {
      let user = await axios({
        method: "get",
        url: `${BASR_URL}/myself`,
        withCredentials: true,
      })
      console.log(user)
      if (user.data.success) {
        // this.$router.replace("/");
        this.loged = true;
        this.user = user.data;
      }
    },
    back() {
      this.$router.back();
    },
    async login() {
      if (this.loginData.account && this.loginData.password && this.codes) {
        var bodyFormData = new FormData();
        bodyFormData.append('username', this.loginData.account);
        bodyFormData.append('password', this.loginData.password);
        bodyFormData.append('remember-me', true);
        bodyFormData.append('imageCode', this.codes);

        try {
          let res = await axios({
            method: "post",
            url: `${BASR_URL}/login`,
            data: bodyFormData,
            headers: { "Content-Type": "multipart/form-data", },
            withCredentials: true,
          })
          console.log(res)
          if (res.data.code === 200) {
            // this.$router.replace("/");
            await this.checkUser()
          }
        } catch (error) {
          console.log(error)
        }

      } else {
        alert("请将信息输入完整！")
      }
    }
    // ...mapActions(["addSong", "randomPlay"]),
  },
};
</script>

<style scoped lang="scss">
.user-center {
  position: fixed;
  top: 0;
  bottom: 0;
  z-index: 100;
  width: 100%;
  background: $color-background;
  .back {
    position: absolute;
    top: 0;
    left: 6px;
    z-index: 50;
    .icon-back {
      display: block;
      padding: 10px;
      font-size: $font-size-large-x;
      color: $color-theme;
    }
  }
  .switches-wrapper {
    margin: 10px 0 30px 0;
  }
  .play-btn {
    box-sizing: border-box;
    width: 135px;
    padding: 7px 0;
    margin: 0 auto;
    text-align: center;
    border: 1px solid $color-text-l;
    color: $color-text-l;
    border-radius: 100px;
    font-size: 0;
    .icon-play {
      display: inline-block;
      vertical-align: middle;
      margin-right: 6px;
      font-size: $font-size-medium-x;
    }
    .text {
      display: inline-block;
      vertical-align: middle;
      font-size: $font-size-small;
    }
  }
  .list-wrapper {
    position: absolute;
    top: 110px;
    bottom: 0;
    width: 100%;
    .list-scroll {
      height: 100%;
      overflow: hidden;

      .user-profile {
        display: flex;
        align-items: center;
        padding: 0 20px 20px 20px;
        .avator {
          background: #888;
          border-radius: 50%;
          height: 60px;
          width: 60px;
        }
        .user-name {
          padding: 0 10px;
          flex: 1;
          text-overflow: ellipsis;
          overflow: hidden;
          white-space: nowrap;
        }
      }

      .search-input-wrapper {
        display: flex;
        flex-direction: column;
        .input-btn {
          height: 35px;
        }
        .imageCode {
          display: flex;
          // padding: 10px;
          margin: 5px 0 10px 0;
          height: 36px;
          .img {
            height: 36px;
            flex: 1;
          }
          .code {
            min-width: 0;
            flex: 2;
            padding-left: 10px;
          }
        }
      }
      .list-inner {
        padding: 20px 30px;
      }
    }
  }
}
</style>
