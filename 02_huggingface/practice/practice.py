from fastapi import FastAPI
from routers import route

app = FastAPI()

app.include_router(route.router)

