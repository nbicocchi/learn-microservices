# Environment Variables

In modern software development, it's crucial to separate configuration from code. 
**Environment variables** are key to achieving this. They're dynamic, named values that are external to your application's source code, allowing you to change the behavior of your application without modifying the code itself.

Why are they so important?

- **Security**: They let you manage sensitive information like API keys, database credentials, and secret keys securely. Instead of hard-coding them directly into your application, you store them as environment variables. This prevents them from being accidentally committed to version control systems like Git, where they could be exposed.
- **Flexibility**: Environment variables make your application portable across different environments. You can use one codebase for development, testing, and production, and simply change the environment variables to point to the correct database, API endpoint, or other service for each environment. For example, your DATABASE_URL can point to a local database for development and to a production database when deployed.
- **Decoupling**: They promote a clean separation between your application's logic and its configuration. This makes your code cleaner, more maintainable, and easier to test.


There are two main ways to manage environment variables:

- Manually with `os`
- Using Pydantic


## Os library

`os` is the Python standard library which allows you to interact with system.

An environment variable can be read thanks to `os.getenv`:

```py
import os

db_name = os.getenv("DB_NAME", "my_db")     # "my_db" is the default value
db_password = os.getenv("DB_PASSWORD")      # None as default value
```

> [!TIP]
> Try it on Linux using for example `export DB_PASSWORD=secret`


## Pydantic

First of all, you must install `pydantic-settings` library.

```
pip install pydantic-settings
```

Suppose a `.env` file such as:

```
DATABASE_URL=postgresql://user:password@localhost:5432/mydb
DEBUG=True
API_KEY=secret
```

You can easily access those values using this code:

```py
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    database_url: str
    debug: bool = False # default value
    api_key: str

    # optional, you can specify env file and its encoding
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

settings = Settings()

print(settings.database_url)
print(settings.debug)
print(settings.api_key)
```

Attribute names must be expressed in *lowercase* with respect to its actual environment variable (e.g. `api_key` for `API_KEY`).

> [!TIP]
> `model_config` could be omitted, especially if you will not use `.env` file.

In particular, followed order for environment variables is:

1. Environment variable (i.e., `export ...`)
2. `.env` (if provided)
3. Default value (if provided)

If an attribute doesn't have any default value and no environment variable (or `.env` is provided), then an error will be raised!

In fact, unlike Pydantic BaseModel, default values of BaseSettings **fields are validated by default**. You can disable this behaviour by setting `validate_default=False` either in model_config or on field level by `Field(validate_default=False)`.

```py
class Settings(BaseSettings):
    model_config = SettingsConfigDict(validate_default=False)

    # default won't be validated
    foo: int = 'test'
```

```py
class Settings1(BaseSettings):
    # default won't be validated
    foo: int = Field('test', validate_default=False)
```




