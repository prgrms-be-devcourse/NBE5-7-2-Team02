# Stage 1: Build
FROM node:22 AS builder
WORKDIR /app

COPY package.json .
RUN npm install
COPY . .
RUN npm run build

# Nginx를 기반으로 하는 최종 이미지
FROM nginx:stable-perl

# Nginx 설정 파일 복사
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# mime.types 파일을 복사
COPY nginx/mime.types /etc/nginx/mime.types

# 빌드된 리액트 앱을 Nginx의 HTML 디렉토리로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# 포트 설정
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]