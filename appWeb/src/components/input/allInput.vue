<template>
    <div class="all-input-wrapper">
        <input class="account" v-model="account" placeholder="请输入账号" />
        <input class="password" type="password" v-model="password" placeholder="请输入密码" />
    </div>
</template>
<script>
export default {
    props: {
        modelValue: Object,
    },
    data() {
        return {
            account: this.modelValue.account ? this.modelValue.account : "",
            password: this.modelValue.password ? this.modelValue.password : ""
        }
    },
    created() {
        this.$watch('account', (newQuery) => {
            this.$emit('update:modelValue', { account: newQuery, password: this.password })
        })

        this.$watch('password', (newQuery) => {
            this.$emit('update:modelValue', { account: this.account, password: newQuery })
        })

        this.$watch('modelValue', (newVal) => {
            this.account = this.modelValue.account ? this.modelValue.account : "";
            this.password = this.modelValue.password ? this.modelValue.password : "";
        })
    },
}
</script>
<style lang="scss" scoped>
.all-input-wrapper {
    display: flex;
    flex-direction: column;
    .account {
        padding: 10px;
        margin: 5px 0 5px 0;
        height: 36px;
    }
    .password {
        padding: 10px;
        margin: 5px 0 5px 0;
        height: 36px;
    }
}
</style>