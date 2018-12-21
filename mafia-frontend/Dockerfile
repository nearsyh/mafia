FROM node:10-slim AS builder
ENV NPM_CONFIG_CACHE=npm-cache
WORKDIR /app
ADD ./ /app/
RUN npm install
RUN npm run build


FROM nginx:1.15-alpine
ENV TZ=Asia/Shanghai
ENTRYPOINT ["nginx", "-g", "daemon off;"]
WORKDIR /app
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder ./app/dist/ /app/