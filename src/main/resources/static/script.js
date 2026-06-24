const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};
const DEFAULT_AVATAR = 'data:image/svg+xml;charset=UTF-8,%3Csvg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 120 120"%3E%3Crect width="120" height="120" fill="%23e5e7eb"/%3E%3Ccircle cx="60" cy="45" r="22" fill="%239ca3af"/%3E%3Cpath d="M24 104c6-23 20-34 36-34s30 11 36 34" fill="%239ca3af"/%3E%3C/svg%3E';

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    const statusMessage = document.getElementById('statusMessage');

    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');

        const users = await response.json();
        await renderUserList(users);
        statusMessage.textContent = users.length === 0 ? '등록된 사용자가 없습니다.' : '';
    } catch (error) {
        console.error('Error fetching users:', error);
        statusMessage.textContent = '사용자 목록을 불러오지 못했습니다.';
    }
}

async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${encodeURIComponent(profileId)}`);
        if (!response.ok) throw new Error('Failed to fetch profile');

        const profile = await response.json();

        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return DEFAULT_AVATAR;
    }
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.replaceChildren();

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId ?
            await fetchUserProfile(user.profileId) :
            DEFAULT_AVATAR;

        const avatar = document.createElement('img');
        avatar.src = profileUrl;
        avatar.alt = `${user.username} 프로필`;
        avatar.className = 'user-avatar';

        const userInfo = document.createElement('div');
        userInfo.className = 'user-info';

        const userName = document.createElement('div');
        userName.className = 'user-name';
        userName.textContent = user.username;

        const userEmail = document.createElement('div');
        userEmail.className = 'user-email';
        userEmail.textContent = user.email;

        const statusBadge = document.createElement('div');
        statusBadge.className = `status-badge ${user.online ? 'online' : 'offline'}`;
        statusBadge.textContent = user.online ? '온라인' : '오프라인';

        userInfo.append(userName, userEmail);
        userElement.append(avatar, userInfo, statusBadge);

        userListElement.appendChild(userElement);
    }
}
