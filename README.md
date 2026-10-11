# Cinema Tickets Code Test

### Requirements

| Scenario                           | Expected result                  |
| ---------------------------------- | ---------------------------------|
| Account ID is zero or negative     | Reject booking                   |
| Empty booking or zero tickets      | Reject booking                   |
| Negative or zero ticket count      | Reject booking                   |
| No ADULT ticket                    | Reject booking                   |
| More INFANT than ADULT             | Reject booking                   |
| More than 25 tickets               | Reject booking                   |
| Valid booking                      | Process payment and reserve seats|


### Assumptions

- One infant per adult lap
- Requests with negative ticket count should be rejected
- Request with zero tickets requests should be rejected
- openapi spec is out of line with controller - using code as source of truth.

### Approach

- Turn specifications into requirements
- TDD
- Start with request validation, create new validation class for separation of concerns.
- integrate validation class and integration test behaviour.
- Calculate payment amount and integrate payments service.
- Calculate number of seats and itegrate reservation service.
- Add mock third party services to integration tests and verify interaction.
