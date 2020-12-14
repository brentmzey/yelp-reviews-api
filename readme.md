# Yelp API

## Endpoints

1. Search by Yelp Business ID

- /reviews/{yelpBusinessId}

- **Example**
  - /reviews/mhrW9O0O5hXGXGnEYBVoag

    - *JSON Response*

```json
{
  "businessName": "Jacques-Imo's Cafe",
  "businessId": "mhrW9O0O5hXGXGnEYBVoag",
  "city": "New Orleans",
  "zipCode": "70118",
  "reviews": [
    {
      "rating": 5,
      "review": "**We dined here before the pandemic in February 2020 and I'm just finishing the review now.\n\nJacques-Imo's was recommended to us by a lot of the locals and...",
      "userName": "Emily C.",
      "userAvatarUrl": "https://s3-media3.fl.yelpcdn.com/photo/mNpj1g0_qjgguGh-CnE3uA/o.jpg"
    },
    {
      "rating": 5,
      "review": "My fiancé and I had an awesome dinner at this restaurant! The customer service was great. Our server was friendly & attentive and did a great job of letting...",
      "userName": "Astrya J.",
      "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/jPINYXmrYLp09geQOTUOjQ/o.jpg"
    },
    {
      "rating": 5,
      "review": "We heard about Jacques-Imo's from an Airbnb guest book. Since COVID-19, we called to make sure they were allowing dine in (they were) and headed right over....",
      "userName": "Vickie M.",
      "userAvatarUrl": null
    }
  ]
}
```

### Search by TERM and LOCATION

- /reviews?term={term}&location={location}

- **Example**

  - /reviews?term=pastry&location=milwaukee

  - *JSON Response*

```json

  "count": 442,
  "limit": 10,
  "data": [
    {
      "businessName": "Greige Patisserie",
      "businessId": "hbHpNAylcxQmssDPcU1G1g",
      "city": "Milwaukee",
      "zipCode": "53204",
      "reviews": [
        {
          "rating": 5,
          "review": "As I'm slowly starting to re-enter the world again, I'm being very picky about where I choose to dine (it just so happens they're across the street from...",
          "userName": "Nell B.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/g8oeZpibeXk-16E3IoiILQ/o.jpg"
        },
        {
          "rating": 5,
          "review": "Ordered curbside takeaway meal a few weeks ago and it was phenomenal.   Order was ready at stated time and they brought it to the car.  We live about 15...",
          "userName": "Jeri A.",
          "userAvatarUrl": null
        },
        {
          "rating": 5,
          "review": "We had curbside supper from here two weeks ago and it was fabulous. It was ready on time was packaged beautifully and still hot when we got home. Looking...",
          "userName": "Kelli G.",
          "userAvatarUrl": null
        }
      ]
    },
    {
      "businessName": "Miss Molly's Cafe & Pastry Shop",
      "businessId": "quWY1Gu2k_UjDf4ZHJp9wQ",
      "city": "Milwaukee",
      "zipCode": "53222",
      "reviews": [
        {
          "rating": 5,
          "review": "We have only tried the raspberry thumbprints, but they are divine. My 7-year old would like to say:worlds best cookie",
          "userName": "Charlotte H.",
          "userAvatarUrl": "https://s3-media4.fl.yelpcdn.com/photo/v4Oepb3ki2JB8c714Bs21g/o.jpg"
        },
        {
          "rating": 5,
          "review": "Fantastic spot. We stopped in on a rainy Saturday for savory brioche and coffee, ended up with a ginger cookie and a oatmeal pecan chocolate chip cookie......",
          "userName": "Vee B.",
          "userAvatarUrl": "https://s3-media2.fl.yelpcdn.com/photo/I6gMGHb-7hJxRa7TVKWzwQ/o.jpg"
        },
        {
          "rating": 5,
          "review": "A quaint little cafe that packs a Parisian punch. Their soup is fabulous (I tried an unforgettable fish chowder), and their pastry is spot-on. Best...",
          "userName": "Brittany J.",
          "userAvatarUrl": null
        }
      ]
    },
    ...
    {
      "businessName": "O&H Danish Bakery",
      "businessId": "j4so6lQwBuiqWvsTpJCPTw",
      "city": "Oak Creek",
      "zipCode": "53154",
      "reviews": [
        {
          "rating": 5,
          "review": "If you're looking for amazing themed cakes or just tasty treats like Italian cookies, this is the place to go. Alex and Antonella are very good at what they...",
          "userName": "Jonny R.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/4NRaGBJadUHFQ2MzNq6YOQ/o.jpg"
        },
        {
          "rating": 5,
          "review": "AMAZING! That's the one word that comes to mind. They made an ADORABLE delicious cake for my son's birthday just the way he requested. Antonella was a...",
          "userName": "Fawn S.",
          "userAvatarUrl": null
        },
        {
          "rating": 4,
          "review": "I have been meaning to get over here for a while and today I finally made it but even though I have traveled the area many times I still couldn't remember...",
          "userName": "Steven T.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/ISSy_2qWm4gf02DjHIWdRw/o.jpg"
        }
      ]
    }
  ]
}
```
