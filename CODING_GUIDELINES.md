# Coding Guidelines and Best Practices

## Naming Conventions

### Acronyms in Names

1. For 4+ letter acronyms, use lowercase. E.g. `LdapUtils`, `HttpURL`

1. For 2 and 3 letter acronyms, use uppercase. E.g. `IOUtils`, `XMLUtils`

### Interfaces and Implementing Classes

1. Do NOT prefix interfaces with `I`.

1. Do NOT postfix implementations with `Impl`.

1. Use normal words for interfaces, e.g. `UserService`.

1. For implementations use the interface name with a meaningful prefix, e.g. `LdapUserService`.

1. If only one implementation exists and an interface is only necessary for technical requirements (e.g. remoting),
   you can prefix the interface name with `Default` and use it as your implementation's class name, e.g. `DefaultUserService`

### Constants (static final)

Use uppercase letters and separate words with underlines, e.g. `MAX_LENGTH`
