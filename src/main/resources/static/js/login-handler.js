// 로그인 페이지 핸들러
import { setAccessToken } from '/js/common-api.js';

// 에러 메시지 초기화
function clearErrorMessages() {
    document.querySelectorAll('.feedback').forEach(el => el.textContent = '');
    document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));
    document.getElementById('message').textContent = '';
}

// 필드별 에러 표시
function displayFieldError(field, message) {
    const errorElement = document.getElementById(field + 'Error');
    const inputElement = document.querySelector(`[name="${field}"]`);

    if (errorElement) {
        errorElement.textContent = message;
    }
    if (inputElement) {
        inputElement.classList.add('is-invalid');
    }
}

// 일반 메시지 표시
function displayMessage(message, isError = true) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.style.color = isError ? '#dc3545' : '#28a745';
}

// 로그인 폼 제출 핸들러
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const form = this;
    const submitBtn = form.querySelector('button[type="submit"]');

    clearErrorMessages();

    // 폼 데이터 수집
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);

    // 버튼 로딩 상태
    submitBtn.disabled = true;
    submitBtn.textContent = '로그인 중...';

    try {
        // API 호출
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const responseData = await response.json().catch(() => ({
            message: '서버 응답 형식이 올바르지 않습니다.'
        }));

        if (response.status === 200 && responseData.accessToken) {
            // 로그인 성공
            setAccessToken(responseData.accessToken);
            alert('🎉 로그인 성공! 메인 페이지로 이동합니다.');
            window.location.href = '/home';

        } else if (response.status === 400) {
            // 유효성 검사 오류
            if (responseData.errors && Array.isArray(responseData.errors)) {
                responseData.errors.forEach(error => {
                    displayFieldError(error.field, error.message);
                });
                displayMessage('❌ 입력 정보를 확인해주세요.');
            } else if (responseData.message) {
                displayMessage('❌ ' + responseData.message);
            }

        } else if (response.status === 401) {
            // 인증 실패
            displayMessage('❌ 이메일 또는 비밀번호가 일치하지 않습니다.');

        } else {
            // 기타 서버 오류
            displayMessage('❌ 서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
        }

    } catch (error) {
        console.error('Network Error:', error);
        displayMessage('🌐 네트워크 연결 오류가 발생했습니다.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '로그인';
    }
});