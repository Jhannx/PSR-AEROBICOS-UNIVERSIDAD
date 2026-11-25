from fastapi import FastAPI
from controller import router
import uvicorn

app = FastAPI(title="API Analítica - PSR Aeróbicos")
app.include_router(router)

if __name__ == "__main__":
    uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)
