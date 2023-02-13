function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }


function sendMSG(data) {
    return $axios({
        'url': '/user/sendMSG',
        'method': 'post',
        data
    })
}



function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })


}

  