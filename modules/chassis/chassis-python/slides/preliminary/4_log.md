# Logging

**Logging** is an essential practice in software development and system administration. It allows developers to record events, errors, and informational messages about the execution of a program. Instead of relying on `print()` statements, Python provides a powerful and flexible logging module that should be used in production code.


Why Logging?

- **Debugging**: Helps track down the root cause of errors.
- **Monitoring**: Provides insights into how a program behaves in real time.
- **Persistence**: Logs can be stored in files, databases, or monitoring systems for later analysis.
- **Control**: Logging allows filtering and categorizing messages by importance.


```py
import logging

logging.debug("This is a debug message.")
logging.info("This is an informational message.")
logging.warning("This is a warning.")
logging.error("This is an error.")
```

In order to configure logging, you must use `logging.basicConfig`

The `basicConfig` function can be use to set the minimum severity level of messages to display. In the following example, all messages with severity INFO and above will appear:

```py
import logging

logging.basicConfig(level=logging.INFO)

logging.debug("This is a debug message.")    # not visible!
logging.info("This is an informational message.")
logging.warning("This is a warning.")
logging.error("This is an error.")
```

Using `basicConfig` we can also:

- Use a custom prefix, e.g. the datetime

```py
logging.basicConfig(
    level=logging.INFO,
    stream=sys.stdout,  # by default stderr is used
    format="[%(asctime)s][%(levelname)s]: %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
)
```

- Store logs in a file

```py
logging.basicConfig(
    filename="app.log",
    filemode="a",  # append mode
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG
)
```
