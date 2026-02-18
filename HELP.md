📂 Estrutura de Pastas do Projeto (Arquitetura Hexagonal)

A Arquitetura Hexagonal (Ports and Adapters) organiza o código em torno do domínio, isolando a lógica de negócio das preocupações externas (banco de dados, UI, etc.).
```
src/main/java/br/com/gama/coupon/
├── application/                     # Lógica da Aplicação (Use Cases, Orquestração)
│   ├── port/                        # Portas Primárias (Interfaces dos Use Cases/Serviços da Aplicação)
│   │   └── in/                      # Portas de entrada (quem quer interagir com o domínio)
│   │       └── CreateCouponUseCase.java
│   │       └── DeleteCouponUseCase.java
│   │       └── GetCouponUseCase.java
│   │       └── UpdateCouponUseCase.java
│   ├── service/                     # Implementações dos Use Cases (Camada de Serviço da Aplicação)
│   │   └── CouponApplicationService.java
│   ├── command/                     # Comandos (DTOs de entrada para os Use Cases)
│   │   └── CreateCouponCommand.java
│   │   └── UpdateCouponCommand.java
│   └── query/                       # Queries (DTOs de saída para os Use Cases)
│       └── CouponResponse.java
│
├── domain/                          # O Coração do Negócio (Entidades, Regras de Negócio, Portas Secundárias)
│   ├── model/                       # Agregados, Entidades e Value Objects
│   │   └── Coupon.java              # Entidade principal do domínio
│   │   └── CouponCode.java          # Value Object para o código do cupom
│   │   └── DiscountValue.java       # Value Object para o valor de desconto
│   │   └── ExpirationDate.java      # Value Object para a data de expiração
│   ├── port/                        # Portas Secundárias (Interfaces de Adapters externos - ex: Repositório)
│   │   └── out/                     # Portas de saída (dependências do domínio para infraestrutura)
│   │       └── CouponRepositoryPort.java # Interface do repositório para o domínio
│   └── exception/                   # Exceções específicas do domínio
│       └── CouponNotFoundException.java
│       └── InvalidCouponException.java
│       └── CouponAlreadyDeletedException.java
│
└── infrastructure/                  # Infraestrutura (Adapters para tecnologias externas, Configurações)
├── adapter/                     # Adapters (Implementações das Portas)
│   ├── in/                      # Adapters de Entrada (Web Controllers que chamam os Use Cases)
│   │   └── web/
│   │       └── CouponController.java
│   │       └── dto/             # DTOs específicos para a camada web
│   │           └── CouponRequestDTO.java
│   │           └── CouponResponseDTO.java
│   └── out/                     # Adapters de Saída (Implementações de Repositórios, Comunicação Externa)
│       └── persistence/
│           └── jpa/             # Adapters JPA para persistência
│               └── CouponJpaEntity.java      # Entidade JPA (Data Model)
│               └── JpaCouponRepository.java  # Repositório Spring Data JPA
│               └── JpaCouponRepositoryAdapter.java # Implementa CouponRepositoryPort
├── config/                      # Configurações gerais da aplicação
│   └── OpenApiConfig.java       # Configuração do Swagger/OpenAPI
│   └── ApplicationConfig.java   # Configurações gerais (Beans, Mappers)
│   └── AuditConfig.java         # Configuração de auditoria (CreatedAt, UpdatedAt)
├── exception/                   # Tratamento de exceções da infraestrutura/global
│   └── GlobalExceptionHandler.java
└── CouponApplication.java       # Ponto de entrada da aplicação Spring Boot

src/main/resources/
├── application.yml                  # Configurações da aplicação (portas, banco de dados, etc.)
└── data.bak.sql                         # Dados iniciais para o H2 (opcional)

src/test/java/br/com/gama/coupon/
├── application/                     # Testes dos Use Cases
│   └── service/
│       ├── CreateCouponServiceTest.java
│       └── DeleteCouponServiceTest.java
├── domain/                          # Testes dos Modelos e Serviços de Domínio
│   └── model/
│       └── CouponTest.java
│       └── CouponCodeTest.java
├── infrastructure/                  # Testes de Adapters e Integração
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── CreateCouponControllerIntegrationTest.java # Testes de integração do Controller
│   │       └── DeleteCouponControllerIntegrationTest.java # Testes de integração do Controller
│   └── out/
│       └── persistence/
│           └── jpa/
│               └── JpaCouponRepositoryAdapterIntegrationTest.java # Testes de integração com o banco (Testcontainers)
└── config/
    └── OpenApiConfigTest.java

src/test/resources/
└── application-test.yml             # Configurações específicas para testes (ex: porta random)

Dockerfile                           # Dockerfile para build da imagem da aplicação
docker-compose.yml                   # Docker Compose para orquestração (aplicação + DB)
.gitignore                           # Arquivos para ignorar no Git
README.md                            # Documentação do projeto
```
